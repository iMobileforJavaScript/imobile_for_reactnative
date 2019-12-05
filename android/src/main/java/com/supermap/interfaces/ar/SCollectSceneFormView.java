package com.supermap.interfaces.ar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.supermap.RNUtils.FileUtil;
import com.supermap.ar.highprecision.MeasureView;
import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.DatasourceConnectionInfo;
import com.supermap.data.EncodeType;
import com.supermap.data.EngineType;
import com.supermap.data.FieldInfo;
import com.supermap.data.FieldInfos;
import com.supermap.data.FieldType;
import com.supermap.data.GeoLine3D;
import com.supermap.data.GeoPoint3D;
import com.supermap.data.Point3D;
import com.supermap.data.Point3Ds;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.Recordset;
import com.supermap.data.Workspace;
import com.supermap.interfaces.ai.CustomRelativeLayout;
import com.supermap.interfaces.ar.rajawali.MotionRajawaliRenderer;
import com.supermap.interfaces.ar.rajawali.ViewMode;
import com.supermap.interfaces.mapping.SMap;
import com.supermap.mapping.MapControl;
import com.supermap.plugin.LocationManagePlugin;
import com.supermap.smNative.collector.SMCollector;

import org.rajawali3d.scene.ASceneFrameCallback;
import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SCollectSceneFormView extends ReactContextBaseJavaModule {

    public static final String REACT_CLASS = "SCollectSceneFormView";
    private static RajawaliSurfaceView mSurfaceView;
    private static MeasureView mMeasureView;

    private static Context mContext = null;
    private static ReactApplicationContext mReactContext = null;
    private static CustomRelativeLayout mCustomRelativeLayout = null;

    private static MotionRajawaliRenderer mRenderer;
    private static boolean isShowTrace = false;//初始值
    private static float[] mCurrentPoseTranslation = new float[3];
    private static float[] mCurrentPoseRotation = new float[4];

    private static DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    private ArrayList<Point3D> mPostData = new ArrayList<>();

    List<SceneFormInfo> infoList = new ArrayList<>();
    private ArrayList<Point3D> mQueryData = new ArrayList<>();//查询到的数据

    private String mDatasourceAlias, mDatasetName, mPointDatasetName = null;
    private static String mLanguage = "CN";//EN
    private static String UDBpath = "";

    public static void setMeasureView(MeasureView measureView) {
        mMeasureView = measureView;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public SCollectSceneFormView(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mContext = reactContext.getApplicationContext();
    }

    public static void setViewManager(CustomRelativeLayout relativeLayout) {
        mCustomRelativeLayout = relativeLayout;
    }


    @SuppressLint("ClickableViewAccessibility")
    public static void setSurfaceView(RajawaliSurfaceView surfaceView) {
        //----------------------------显示层----------------------------------
        //1.获取显示层
        mSurfaceView = surfaceView;

        //2. 设置透明
        mSurfaceView.setFrameRate(60);
        mSurfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);
//        mSurfaceView.setTransparent(false);

        //3.新建渲染器
        mRenderer = new MotionRajawaliRenderer(mContext);

        //4.添加手势控制
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRenderer.onTouchEvent(event);
                return true;
            }
        });

        //5.控制渲染
        setupRenderer();
    }

    private static void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


    private static void setupRenderer() {
        // motion renderer
        mSurfaceView.setEGLContextClientVersion(2);
        mRenderer.getCurrentScene().registerFrameCallback(new ASceneFrameCallback() {
            @Override
            public void onPreFrame(long sceneTime, double deltaTime) {
                mRenderer.updateCameraPoseFromVecotr(mCurrentPoseTranslation, mCurrentPoseRotation, mReactContext);
                mMeasureView.saveCurrentPoseTranslation(mCurrentPoseTranslation);
                mMeasureView.saveCurrentPoseRotation(mCurrentPoseRotation);
                if (isShowTrace) {
                    //记录总长度
                    float totalLength = mRenderer.getTotalLength();

                    WritableMap allResults = Arguments.createMap();
                    allResults.putString("totalLength", mDecimalFormat.format(totalLength));
                    sendEvent(mReactContext, "onTotalLengthChanged", allResults);
                }
            }

            @Override
            public boolean callPreFrame() {
                return true;
            }

            @Override
            public void onPreDraw(long sceneTime, double deltaTime) {
            }

            @Override
            public void onPostFrame(long sceneTime, double deltaTime) {

            }
        });

        mSurfaceView.setSurfaceRenderer(mRenderer);
    }

    /**
     * 开始记录
     *
     * @param promise
     */
    @ReactMethod
    public void startRecording(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------startRecording--------RN--------");
            mRenderer.addNewRoute();
            isShowTrace = true;
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 停止记录
     *
     * @param promise
     */
    @ReactMethod
    public void stopRecording(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------stopRecording--------RN--------");
            isShowTrace = false;

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 获取轨迹状态
     *
     * @param promise
     */
    @ReactMethod
    public void isShowTrace(Promise promise) {
        try {
            promise.resolve(isShowTrace);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 获取三维场景里的数据并保存
     *
     * @param promise
     */
    @ReactMethod
    public void saveData(String name, Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------saveData--------RN--------");
            if (mPostData == null) {
                mPostData = new ArrayList<>();
            } else {
                mPostData.clear();
            }
            if (mRenderer != null) {
                mRenderer.savePoseData(mPostData);
                mRenderer.saveCurrentRoute();
            }
            //保存到数据集
            saveDataset(name);

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 删除指定数据
     *
     * @param promise
     */
    @ReactMethod
    public void deleteData(int index, Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------saveData--------RN--------");

            deleteDataset(index);

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 保存当前定位点
     *
     * @param promise
     */
    @ReactMethod
    public void saveGPSData(String name, Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------saveData--------RN--------");

            LocationManagePlugin.GPSData gpsDat = SMCollector.getGPSPoint();
            Point3D point3D = new Point3D(gpsDat.dLongitude, gpsDat.dLatitude, gpsDat.dAltitude);
            //保存到数据集
            saveDataset(name, point3D);

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }


    /**
     * 判断数据集类型
     *
     * @param promise
     */
    @ReactMethod
    public void isLineDataset(int index, Promise promise) {
        try {
            Datasource datasource = SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().get(this.mDatasourceAlias);
            Datasets datasets = datasource.getDatasets();
            Dataset dataset = datasets.get(index);
            if(dataset.getType().equals(DatasetType.LINE3D)){
                promise.resolve(true);
            } else if(dataset.getType().equals(DatasetType.POINT3D)){
                promise.resolve(false);
            }
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 加载数据到三维场景里
     *
     * @param promise
     */
    @ReactMethod
    public void loadData(int index, boolean isLine, Promise promise) {
        try {
            if (infoList == null) {
                infoList = new ArrayList<>();
            } else {
                infoList.clear();
            }
            if (isLine) {
                infoList = getDatasetAllGeometry(this.mDatasourceAlias, index);
            } else {
                infoList = getPointDatasetAllGeometry(this.mDatasourceAlias, index);
            }

            Log.d(REACT_CLASS, "----------------loadData--------RN--------");

            if (mQueryData == null) {
                mQueryData = new ArrayList<>();
            } else {
                mQueryData.clear();
            }
            if (isLine) {
                if (infoList != null && infoList.size() > 0) {
                    SceneFormInfo info = infoList.get(0);
                    GeoLine3D geoLine3D = info.getGeoLine3D();
                    for (int i = 0; i < geoLine3D.getPartCount(); i++) {
                        Point3Ds part = geoLine3D.getPart(i);
                        Point3D[] point3DS = part.toArray();
                        for (Point3D point : point3DS) {
                            mQueryData.add(point);
                        }
                    }

                    if (mRenderer != null) {
                        mRenderer.loadPoseData(mQueryData);
                    }
                    promise.resolve(true);
                } else {
                    promise.resolve(false);
                }
            } else {
                if (infoList != null && infoList.size() > 0) {
                    SceneFormInfo info = infoList.get(0);
                    GeoPoint3D geoPoint3D = info.getGeoPoint3D();
                    Point3D point = geoPoint3D.getInnerPoint3D();
                    mQueryData.add(point);

                    if (mRenderer != null) {
                        mRenderer.loadPoseData(mQueryData);
                    }
                    promise.resolve(true);
                } else {
                    promise.resolve(false);
                }
            }

        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 清空当前数据
     *
     * @param promise
     */
    @ReactMethod
    public void clearData(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------clearData--------RN--------");
            if (mRenderer != null) {
//                mRenderer.clearPoseData();
                mRenderer.clearCurrentRoute();
            }
            if (mPostData != null) {
                mPostData.clear();
            }
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }


    /**
     * 清空所有数据
     *
     * @param promise
     */
    @ReactMethod
    public void clearAllData(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------clearData--------RN--------");
            if (mRenderer != null) {
                mRenderer.clearPoseData();
//                mRenderer.clearCurrentRoute();
            }
            if (mPostData != null) {
                mPostData.clear();
            }
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setArSceneViewVisible(final boolean isVisible, Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------setArSceneViewVisible--------RN--------");
//            getCurrentActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (isVisible) {
//                        mMeasureView.setVisibility(View.VISIBLE);
//                    } else {
//                        mMeasureView.setVisibility(View.INVISIBLE);
//                    }
//                }
//            });

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void onDestroy(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------onDestroy--------RN--------");
            if (mRenderer != null) {
                mRenderer.getCurrentScene().clearFrameCallbacks();
            }

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void initSceneFormView(String datasourceAlias, String datasetName, String datasetPointName, String language, String UDBpath, Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------initSceneFormView--------RN--------");
            mDatasourceAlias = datasourceAlias;
            mDatasetName = datasetName;
            mPointDatasetName = datasetPointName;
            mLanguage = language;
            this.UDBpath = UDBpath;

            createDatasource(mDatasourceAlias, this.UDBpath);
//            createDataset(mDatasourceAlias, mDatasetName);
//            createPointDataset(mDatasourceAlias, mPointDatasetName);

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getHistoryData(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------getHistoryData--------RN--------");

            Datasource datasource = SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().get(this.mDatasourceAlias);
            Datasets datasets = datasource.getDatasets();
            WritableArray arr = Arguments.createArray();
            if(datasets.getCount()>0){
                for (int i = 0; i < datasets.getCount(); i++){
                    WritableMap writeMap = Arguments.createMap();
                    writeMap.putString("name", datasets.get(i).getName());
                    writeMap.putInt("index", i);
                    writeMap.putBoolean("select",false);
                    arr.pushMap(writeMap);
                }
                WritableMap writableMap = Arguments.createMap();
                writableMap.putArray("history", arr);
                promise.resolve(writableMap);
            } else {
                promise.resolve(false);
            }


        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void setDataSource(String name, String path, Promise promise) {
        try {
            mDatasourceAlias = name;
            boolean exit = false;
            int count = SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().getCount();
            for (int i = 0; i < count; i++) {
                if (SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().get(i).getAlias().equals(name)) {
                    exit = true;
                }
            }
            if (!exit) {
                DatasourceConnectionInfo datasourceconnection = new DatasourceConnectionInfo();
                // 设置文件数据源连接需要的参数
                datasourceconnection.setEngineType(EngineType.UDB);
                datasourceconnection.setServer(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + path);
                datasourceconnection.setAlias(name);
                Datasource datasource = SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().open(datasourceconnection);
            }
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 切换观看模式
     *
     * @param promise
     */
    @ReactMethod
    public void switchViewMode(Promise promise) {
        try {
            Log.d(REACT_CLASS, "----------------switchViewMode--------RN--------");
            ViewMode viewMode = mRenderer.getViewMode();
            if (viewMode == ViewMode.FIRST_PERSON) {
                mRenderer.setViewMode(ViewMode.THIRD_PERSON);
            } else {
                mRenderer.setViewMode(ViewMode.FIRST_PERSON);
            }

            promise.resolve(true);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /*********************************************************************************************/
    private void createDatasource(String datasourceAlias, String datasourcePath) {
        Datasource datasource = SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().get(datasourceAlias);
        if (datasource == null || datasource.isReadOnly()) {
            DatasourceConnectionInfo info = new DatasourceConnectionInfo();
            info.setAlias(datasourceAlias);
            info.setEngineType(EngineType.UDB);
            FileUtil.createDirectory(datasourcePath);
            String server = datasourcePath + datasourceAlias + ".udb";
            info.setServer(server);

            datasource = SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().create(info);
            if (datasource == null) {
                SMap.getInstance().getSmMapWC().getWorkspace().getDatasources().open(info);
            } else {
                Log.e(REACT_CLASS, "createDatasource: " + server);
            }
            info.dispose();
        }
    }

    private void createDataset(String UDBName, String datasetName) {
        MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
        Workspace workspace = mapControl.getMap().getWorkspace();
        Datasource datasource = workspace.getDatasources().get(UDBName);

        Datasets datasets = datasource.getDatasets();
        if (datasets.contains(datasetName)) {
            checkFieldInfos((DatasetVector) datasets.get(datasetName));
            return;
        }

        DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
        datasetVectorInfo.setType(DatasetType.LINE3D);
        datasetVectorInfo.setEncodeType(EncodeType.NONE);
        datasetVectorInfo.setName(datasetName);
        DatasetVector datasetVector = datasets.create(datasetVectorInfo);

        //创建数据集时创建好字段
        addFieldInfo(datasetVector, "Description", FieldType.TEXT, false, "", 255);
        addFieldInfo(datasetVector, "ModifiedDate", FieldType.TEXT, false, "", 255);
        addFieldInfo(datasetVector, "Name", FieldType.TEXT, false, "", 255);

        datasetVector.setPrjCoordSys(new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE));

        datasetVectorInfo.dispose();
        datasetVector.close();
    }

    private void createPointDataset(String UDBName, String pointdatasetName) {
        MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
        Workspace workspace = mapControl.getMap().getWorkspace();
        Datasource datasource = workspace.getDatasources().get(UDBName);

        Datasets datasets = datasource.getDatasets();
        if (datasets.contains(pointdatasetName)) {
            checkFieldInfos((DatasetVector) datasets.get(pointdatasetName));
            return;
        }

        DatasetVectorInfo datasetVectorInfo = new DatasetVectorInfo();
        datasetVectorInfo.setType(DatasetType.POINT3D);
        datasetVectorInfo.setEncodeType(EncodeType.NONE);
        datasetVectorInfo.setName(pointdatasetName);
        DatasetVector datasetVector = datasets.create(datasetVectorInfo);

        //创建数据集时创建好字段
        addFieldInfo(datasetVector, "Description", FieldType.TEXT, false, "", 255);
        addFieldInfo(datasetVector, "ModifiedDate", FieldType.TEXT, false, "", 255);
        addFieldInfo(datasetVector, "Name", FieldType.TEXT, false, "", 255);

        datasetVector.setPrjCoordSys(new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE));

        datasetVectorInfo.dispose();
        datasetVector.close();
    }

    // 添加指定字段到数据集
    private void addFieldInfo(DatasetVector dv, String name, FieldType type, boolean required, String value, int maxLength) {
        FieldInfos infos = dv.getFieldInfos();
        if (infos.indexOf(name) != -1) {//exists
            infos.remove(name);
        }
        FieldInfo newInfo = new FieldInfo();
        newInfo.setName(name);
        newInfo.setType(type);
        newInfo.setMaxLength(maxLength);
        newInfo.setDefaultValue(value);
        newInfo.setRequired(required);
        infos.add(newInfo);
    }

    private void checkFieldInfos(DatasetVector datasetVector) {
        FieldInfos fieldInfos = datasetVector.getFieldInfos();

        if (fieldInfos.indexOf("Description") == -1) {
            addFieldInfo(datasetVector, "Description", FieldType.TEXT, false, "", 255);
        }

        if (fieldInfos.indexOf("ModifiedDate") == -1) {
            addFieldInfo(datasetVector, "ModifiedDate", FieldType.TEXT, false, "", 255);
        }

        if (fieldInfos.indexOf("Name") == -1) {
            addFieldInfo(datasetVector, "Name", FieldType.TEXT, false, "", 255);
        }
    }

    /**
     * 保存数据
     */
    private void saveDataset(String name) {
        try {
            mDatasetName = name;
            createDataset(mDatasourceAlias, mDatasetName);

            DatasetVector datasetVector = null;
            MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
            Workspace workspace = mapControl.getMap().getWorkspace();
            Datasource datasource = workspace.getDatasources().get(mDatasourceAlias);
            datasetVector = (DatasetVector) datasource.getDatasets().get(mDatasetName);
            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);//动态指针
            if (recordset != null) {
                //移动指针到最后
                recordset.moveLast();
                recordset.edit();//可编辑

                GeoLine3D geoLine3D = new GeoLine3D();
                Point3D[] point3DS = new Point3D[mPostData.size()];
                for (int i = 0; i < mPostData.size(); i++) {
                    point3DS[i] = mPostData.get(i);
                }
                Point3Ds point3Ds = new Point3Ds(point3DS);
                geoLine3D.addPart(point3Ds);
                //移动指针到下一位
                recordset.moveNext();
                //新增面对象
                recordset.addNew(geoLine3D);

                FieldInfos fieldInfos = recordset.getFieldInfos();
                if (fieldInfos.indexOf("ModifiedDate") != -1) {
                    String str = null;
                    Object ob = recordset.getFieldValue("ModifiedDate");
                    if (ob != null) {
                        str = ob.toString();
                    }
                    if (!getCurrentTime().equals(str)) {
                        recordset.setFieldValue("ModifiedDate", getCurrentTime());
                    }
                }
                if (fieldInfos.indexOf("NAME") != -1) {
                    String str = null;
                    Object ob = recordset.getFieldValue("NAME");
                    if (ob != null) {
                        str = ob.toString();
                    }
                    if (!name.equals(str)) {
                        recordset.setFieldValue("NAME", name);
                    }
                }

                //保存更新,并释放资源
                recordset.update();
                recordset.close();
                recordset.dispose();
            }
        } catch (Exception e) {
            Log.e(REACT_CLASS, e.getMessage());
        }

    }


    /**
     * 保存点数据
     */
    private void saveDataset(String name, Point3D point3D) {
        try {
            mPointDatasetName = name;
            createPointDataset(mDatasourceAlias, mPointDatasetName);

            DatasetVector datasetVector = null;
            MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
            Workspace workspace = mapControl.getMap().getWorkspace();
            Datasource datasource = workspace.getDatasources().get(mDatasourceAlias);
            datasetVector = (DatasetVector) datasource.getDatasets().get(mPointDatasetName);

            Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);//动态指针
            if (recordset != null) {
                //移动指针到最后
                recordset.moveLast();
                recordset.edit();//可编辑

                GeoPoint3D geoPoint3D = new GeoPoint3D(point3D);
                //移动指针到下一位
                recordset.moveNext();
                //新增面对象
                recordset.addNew(geoPoint3D);

                FieldInfos fieldInfos = recordset.getFieldInfos();
                if (fieldInfos.indexOf("ModifiedDate") != -1) {
                    String str = null;
                    Object ob = recordset.getFieldValue("ModifiedDate");
                    if (ob != null) {
                        str = ob.toString();
                    }
                    if (!getCurrentTime().equals(str)) {
                        recordset.setFieldValue("ModifiedDate", getCurrentTime());
                    }
                }
                if (fieldInfos.indexOf("NAME") != -1) {
                    String str = null;
                    Object ob = recordset.getFieldValue("NAME");
                    if (ob != null) {
                        str = ob.toString();
                    }
                    if (!getCurrentTime().equals(str)) {
                        recordset.setFieldValue("NAME", getCurrentTime());
                    }
                }

                //保存更新,并释放资源
                recordset.update();
                recordset.close();
                recordset.dispose();
            }
        } catch (Exception e) {
            Log.e(REACT_CLASS, e.getMessage());
        }
    }

    /**
     * 删除数据
     */
    private void deleteDataset(int index) {
        try {
            DatasetVector datasetVector = null;
            MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
            Workspace workspace = mapControl.getMap().getWorkspace();
            Datasource datasource = workspace.getDatasources().get(mDatasourceAlias);
            datasource.getDatasets().delete(index);
        } catch (Exception e) {
            Log.e(REACT_CLASS, e.getMessage());
        }

    }


    private String getCurrentTime() {
        //得到long类型当前时间
        long l = System.currentTimeMillis();
        //new日期对
        Date date = new Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_ms", Locale.CHINA);

        return dateFormat.format(date);
    }

    /**
     * 获取指定数据集中所有信息
     */
    public List<SceneFormInfo> getDatasetAllGeometry(String UDBName, int index) {
        DatasetVector datasetVector = null;
        if (UDBName != null) {
            MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
            Workspace workspace = mapControl.getMap().getWorkspace();
            Datasource datasource = workspace.getDatasources().get(UDBName);
            if (datasource != null) {
                datasetVector = (DatasetVector) datasource.getDatasets().get(index);
                datasetVector.open();
            }
        }

        if (datasetVector == null) {
            Log.e(REACT_CLASS, "getDatasetAllGeometry argument is valid!");
            return null;
        }

        if (datasetVector.getType() != DatasetType.LINE3D) {
            Log.e(REACT_CLASS, "DatasetType is not LINE3D!");
            return null;
        }

        // 如果数据集未打开
        if (!datasetVector.isOpen()) {
            return null;
        }

        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        recordset.moveFirst();
        Object ob;
        FieldInfos fieldInfos = recordset.getFieldInfos();

        ArrayList<SceneFormInfo> list = new ArrayList<SceneFormInfo>();

        while (!recordset.isEOF()) {
            int geometryId = recordset.getGeometry().getID();

            GeoLine3D geoLine3D = (GeoLine3D) recordset.getGeometry();

            String NAME = "";
            if (fieldInfos.indexOf("NAME") != -1) {
                ob = recordset.getFieldValue("NAME");
                if (ob != null) {
                    NAME = ob.toString();
                }
            }

            String TIME = "";
            if (fieldInfos.indexOf("ModifiedDate") != -1) {
                ob = recordset.getFieldValue("ModifiedDate");
                if (ob != null) {
                    TIME = ob.toString();
                }
            }

            String Description = "";
            if (fieldInfos.indexOf("Description") != -1) {
                ob = recordset.getFieldValue("Description");
                if (ob != null) {
                    Description = ob.toString();
                }
            }

            SceneFormInfo info = new SceneFormInfo.Builder()
                    .ID(geometryId)
                    .geoLine3D(geoLine3D)
                    .name(NAME)
                    .time(TIME)
                    .notes(Description)
                    .build();

            list.add(info);

            recordset.moveNext();
        }

        recordset.close();
        recordset.dispose();
        return list;
    }

    /**
     * 获取指定数据集中所有信息
     */
    public List<SceneFormInfo> getPointDatasetAllGeometry(String UDBName, int index) {
        DatasetVector datasetVector = null;
        if (UDBName != null) {
            MapControl mapControl = SMap.getInstance().getSmMapWC().getMapControl();
            Workspace workspace = mapControl.getMap().getWorkspace();
            Datasource datasource = workspace.getDatasources().get(UDBName);
            datasetVector = (DatasetVector) datasource.getDatasets().get(index);
        }

        if (datasetVector == null) {
            Log.e(REACT_CLASS, "getDatasetAllGeometry argument is valid!");
            return null;
        }


        // 如果数据集未打开
        if (!datasetVector.isOpen()) {
            return null;
        }

        Recordset recordset = datasetVector.getRecordset(false, CursorType.STATIC);
        recordset.moveFirst();
        Object ob;
        FieldInfos fieldInfos = recordset.getFieldInfos();

        ArrayList<SceneFormInfo> list = new ArrayList<SceneFormInfo>();

        while (!recordset.isEOF()) {
            int geometryId = recordset.getGeometry().getID();

            GeoPoint3D geoPoint3D = (GeoPoint3D) recordset.getGeometry();

            String NAME = "";
            if (fieldInfos.indexOf("NAME") != -1) {
                ob = recordset.getFieldValue("NAME");
                if (ob != null) {
                    NAME = ob.toString();
                }
            }

            String TIME = "";
            if (fieldInfos.indexOf("ModifiedDate") != -1) {
                ob = recordset.getFieldValue("ModifiedDate");
                if (ob != null) {
                    TIME = ob.toString();
                }
            }

            String Description = "";
            if (fieldInfos.indexOf("Description") != -1) {
                ob = recordset.getFieldValue("Description");
                if (ob != null) {
                    Description = ob.toString();
                }
            }

            SceneFormInfo info = new SceneFormInfo.Builder()
                    .ID(geometryId)
                    .geoPoint3D(geoPoint3D)
                    .name(NAME)
                    .time(TIME)
                    .notes(Description)
                    .build();

            list.add(info);

            recordset.moveNext();
        }

        recordset.close();
        recordset.dispose();
        return list;
    }

}
