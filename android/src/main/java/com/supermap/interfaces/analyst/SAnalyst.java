package com.supermap.interfaces.analyst;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.supermap.analyst.BufferAnalyst;
import com.supermap.analyst.BufferAnalystGeometry;
import com.supermap.analyst.BufferAnalystParameter;
import com.supermap.analyst.BufferEndType;
import com.supermap.analyst.BufferRadiusUnit;
import com.supermap.analyst.networkanalyst.FacilityAnalyst;
import com.supermap.analyst.networkanalyst.FacilityAnalystResult;
import com.supermap.analyst.networkanalyst.FacilityAnalystSetting;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.analyst.networkanalyst.WeightFieldInfos;
import com.supermap.containts.EventConst;
import com.supermap.data.Color;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.DatasetVectorInfo;
import com.supermap.data.Datasource;
import com.supermap.data.EncodeType;
import com.supermap.data.Enum;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoRegion;
import com.supermap.data.GeoStyle;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.Size2D;
import com.supermap.data.Workspace;
import com.supermap.distributeanalystservices.AggregatePointsOnline;
import com.supermap.distributeanalystservices.DensityAnalystOnline;
import com.supermap.distributeanalystservices.DistributeAnalystListener;
import com.supermap.interfaces.mapping.SMap;
import com.supermap.mapping.Action;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerSetting;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.MapControl;
import com.supermap.mapping.Selection;
import com.supermap.mapping.TrackingLayer;
import com.supermap.smNative.Network_tool;
import com.supermap.smNative.SMAnalyst;
import com.supermap.smNative.SMLayer;
import com.supermap.smNative.SMParameter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class SAnalyst extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "SAnalyst";
    private static SAnalyst analyst;
    private static ReactApplicationContext context;
    //    private LongPressAction longPressAction = LongPressAction.NULL;
    private ArrayList<Integer> m_elementIDs = null;
    private FacilityAnalyst facilityAnalyst = null;
    private TransportationAnalyst transportationAnalyst = null;

    private GeoStyle getGeoStyle(Size2D size2D, Color color) {
        GeoStyle geoStyle = new GeoStyle();
        geoStyle.setMarkerSize(size2D);
        geoStyle.setLineColor(color);
        return geoStyle;
    }

    ReactContext mReactContext;

    public SAnalyst(ReactApplicationContext context) {
        super(context);
        this.context = context;
        mReactContext = context;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    /******************************************************************************缓冲区分析*****************************************************************************************/

    @ReactMethod
    public void analystBuffer(String layerPath, ReadableMap params, Promise promise) {
        try {
            com.supermap.mapping.Map map = SMap.getInstance().getSmMapWC().getMapControl().getMap();
            Layer layer = SMLayer.findLayerByPath(layerPath);
            Selection selection = layer.getSelection();
            Recordset recordset = selection.toRecordset();

            TrackingLayer trackingLayer = map.getTrackingLayer();

            trackingLayer.clear();
            while (!recordset.isEOF()) {

                Geometry geoForBuffer = recordset.getGeometry();
                Dataset queryDataset = recordset.getDataset();

                PrjCoordSys prjCoordSys = queryDataset.getPrjCoordSys();

                BufferAnalystParameter bufferAnalystParameter = new BufferAnalystParameter();
                ReadableMap parameter = params.getMap("parameter");

                if (params.hasKey("parameter")) {
                    if (parameter.hasKey("endType")) {
                        BufferEndType endType = (BufferEndType) Enum.parse(BufferEndType.class, parameter.getInt("endType"));
                        bufferAnalystParameter.setEndType(endType);
                    }
                    if (parameter.hasKey("leftDistance")) {
                        bufferAnalystParameter.setLeftDistance(parameter.getInt("leftDistance"));
                    }
                    if (parameter.hasKey("rightDistance")) {
                        bufferAnalystParameter.setRightDistance(parameter.getInt("rightDistance"));
                    }
                }

                GeoRegion geoRegion = BufferAnalystGeometry.createBuffer(geoForBuffer, bufferAnalystParameter, prjCoordSys);

                GeoStyle geoStyle = SMAnalyst.getGeoStyleByDictionary(params.getMap("geoStyle"));
                if (geoStyle != null) {
                    geoRegion.setStyle(geoStyle);
                }

                trackingLayer.add(geoRegion, "");

                recordset.moveNext();
            }
            recordset.dispose();

            map.refresh();
            promise.resolve(true);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }


    /**
     * 创建矢量数据集缓冲区
     */
    @ReactMethod
    public void createBuffer(ReadableMap sourceData, ReadableMap resultData, ReadableMap bufferParameter, boolean isUnion, boolean isAttributeRetained, ReadableMap optionParameter, Promise promise) {
        try {
            com.supermap.mapping.Map map = SMap.getInstance().getSmMapWC().getMapControl().getMap();
            if (map.getWorkspace() == null || map.getWorkspace() != SMap.getInstance().getSmMapWC().getWorkspace()) {
                map.setWorkspace(SMap.getInstance().getSmMapWC().getWorkspace());
            }

            Dataset sourceDataset = SMAnalyst.getDatasetByDictionary(sourceData);
            Dataset resultDataset = null;

            if (resultData.hasKey("dataset")) {
                resultDataset = SMAnalyst.createDatasetByDictionary(resultData);
            }

            boolean result = false;
            String errorMsg = "";

            if (sourceDataset != null && resultDataset != null) {
                BufferAnalystParameter parameter = SMAnalyst.getBufferAnalystParameterByDictionary(bufferParameter);

                if (parameter != null) {
                    result = BufferAnalyst.createBuffer((DatasetVector) sourceDataset, (DatasetVector) resultDataset, parameter, isUnion, isAttributeRetained);

                    if (!result) errorMsg = "分析失败";
                } else {
                    errorMsg = "缺少分析参数";
                }
            } else {
                if (sourceDataset == null) {
                    errorMsg = "数据源不存在";
                } else if (sourceData == null) {
                    errorMsg = "数据集已存在";
                }
            }

            GeoStyle geoStyle = null;
            ReadableMap geoStyleMap;
            if (optionParameter.hasKey("geoStyle")) {
                geoStyle = SMAnalyst.getGeoStyleByDictionary(optionParameter.getMap("geoStyle"));
            } else {
                geoStyle = SMAnalyst.getGeoStyleByDictionary(null);
            }
            if (geoStyle != null) {
                String description = "{\"geoStyle\":" + geoStyle.toJson() + "}";
                resultDataset.setDescription(description);
            }

            if (result && optionParameter.hasKey("showResult")) {
                boolean showResult = optionParameter.getBoolean("showResult");
                if (showResult) {
                    Layer layer = map.getLayers().add(resultDataset, true);
                    if (geoStyle != null) {
                        LayerSetting layerSetting = layer.getAdditionalSetting();
                        ((LayerSettingVector) layerSetting).setStyle(geoStyle);
                    }

                    map.refresh();
                }
            }

            WritableMap resultMap = Arguments.createMap();
            resultMap.putBoolean("result", result);
            if (result) {
                promise.resolve(resultMap);
            } else {
                SMAnalyst.deleteDataset(resultData);
                resultMap.putString("errorMsg", errorMsg);
                promise.resolve(resultMap);
            }
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 多重缓冲区分析
     *
     * @param sourceData
     * @param resultData
     * @param bufferRadiuses
     * @param bufferRadiusUnit
     * @param semicircleSegments
     * @param isUnion
     * @param isAttributeRetained
     * @param isRing
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void createMultiBuffer(ReadableMap sourceData, ReadableMap resultData, ReadableArray bufferRadiuses, String bufferRadiusUnit, int semicircleSegments, boolean isUnion, boolean isAttributeRetained, boolean isRing, ReadableMap optionParameter, Promise promise) {
        try {
            com.supermap.mapping.Map map = SMap.getInstance().getSmMapWC().getMapControl().getMap();
            if (map.getWorkspace() == null || map.getWorkspace() != SMap.getInstance().getSmMapWC().getWorkspace()) {
                map.setWorkspace(SMap.getInstance().getSmMapWC().getWorkspace());
            }

            Dataset sourceDataset = SMAnalyst.getDatasetByDictionary(sourceData);
            Dataset resultDataset = null;

            if (resultData.hasKey("dataset")) {
                resultDataset = SMAnalyst.createDatasetByDictionary(resultData);
            }

            boolean result = false;
            String errorMsg = "";

            if (sourceDataset != null && resultDataset != null) {
                BufferRadiusUnit unit = SMAnalyst.getBufferRadiusUnit(bufferRadiusUnit);

                double[] radiusArr = new double[bufferRadiuses.size()];
                for (int i = 0; i < bufferRadiuses.size(); i++) {
                    radiusArr[i] = bufferRadiuses.getDouble(i);
                }
                result = BufferAnalyst.createMultiBuffer((DatasetVector) sourceDataset, (DatasetVector) resultDataset, radiusArr, unit, semicircleSegments, isUnion, isAttributeRetained, isRing);

                if (!result) errorMsg = "分析失败";
            } else {
                if (sourceDataset == null) {
                    errorMsg = "数据源不存在";
                } else if (sourceData == null) {
                    errorMsg = "数据集已存在";
                }
            }

            GeoStyle geoStyle = null;
            ReadableMap geoStyleMap;
            if (optionParameter.hasKey("geoStyle")) {
                geoStyle = SMAnalyst.getGeoStyleByDictionary(optionParameter.getMap("geoStyle"));
            } else {
                geoStyle = SMAnalyst.getGeoStyleByDictionary(null);
            }
            if (geoStyle != null) {
                String description = "{\"geoStyle\":" + geoStyle.toJson() + "}";
                resultDataset.setDescription(description);
            }

            if (result && optionParameter.hasKey("showResult")) {
                boolean showResult = optionParameter.getBoolean("showResult");
                if (showResult) {
                    Layer layer = map.getLayers().add(resultDataset, true);
                    if (geoStyle != null) {
                        LayerSetting layerSetting = layer.getAdditionalSetting();
                        ((LayerSettingVector) layerSetting).setStyle(geoStyle);
                    }

                    map.refresh();
                }
            }

            WritableMap resultMap = Arguments.createMap();
            resultMap.putBoolean("result", result);
            if (result) {
                promise.resolve(resultMap);
            } else {
                SMAnalyst.deleteDataset(resultData);
                resultMap.putString("errorMsg", errorMsg);
                promise.resolve(resultMap);
            }
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }


    /********************************************************************************叠加分析**************************************************************************************/

    /**
     * 叠加分析-裁剪
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void clip(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("clip", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 叠加分析-擦除
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void erase(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("erase", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 叠加分析-同一
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void identity(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("identity", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 叠加分析-相交
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void intersect(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("intersect", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 叠加分析-合并
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void union(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("union", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 叠加分析-更新
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void update(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("update", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /**
     * 叠加分析-对称差
     *
     * @param sourceData
     * @param targetData
     * @param resultData
     * @param optionParameter
     * @param promise
     */
    @ReactMethod
    public void xOR(ReadableMap sourceData, ReadableMap targetData, ReadableMap resultData, ReadableMap optionParameter, Promise promise) {
        try {
            boolean result = SMAnalyst.overlayerAnalystWithType("xOR", sourceData, targetData, resultData, optionParameter);
            promise.resolve(result);
        } catch (Exception e) {
            SMAnalyst.deleteDataset(resultData);
            promise.reject(e);
        }
    }

    /******************************************************************************在线分析*****************************************************************************************/

    @ReactMethod
    public void getOnlineAnalysisData(String ip, String port, int type, Promise promise) {
        try {
            HttpURLConnection conn = null;
            String jsonStr = "";
            try {
                String strUrl = "";
                switch (type) {
                    case 0:
                        strUrl = "http://" + ip + ":" + port + "/iserver/services/datacatalog/rest/datacatalog/sharefile.rjson";
                        break;
                    case 1:
                        strUrl = "http://" + ip + ":" + port + "/iserver/services/distributedanalyst/rest/v1/jobs/spatialanalyst/density.json";
                        break;
                    case 2:
                        strUrl = "http://" + ip + ":" + port + "/iserver/services/distributedanalyst/rest/v1/jobs/spatialanalyst/aggregatepoints.json";
                        break;
                }
                URL url = new URL(strUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    //读取缓存
                    byte[] buffer = new byte[2048];
                    int length = 0;
                    while ((length = in.read(buffer)) != -1) {
                        bos.write(buffer, 0, length);//写入输出流
                    }
                    in.close();//读取完毕，关闭输入流

                    // 根据输出流创建字符串对象
                    jsonStr = bos.toString("UTF-8");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            promise.resolve(jsonStr);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 在线分析-密度分析
     *
     * @param serverInfo
     * @param analysisData
     * @param promise
     */
    @ReactMethod
    public void densityOnline(ReadableMap serverInfo, ReadableMap analysisData, Promise promise) {
        try {
            String errorMsg = "";
            if (!serverInfo.hasKey("ip")) {
                errorMsg += "ip";
            }

            if (!serverInfo.hasKey("port")) {
                errorMsg += "port";
            }

            if (!analysisData.hasKey("datasetName")) {
                errorMsg += "DatasetName";
            }

            if (!analysisData.hasKey("analystMethod")) {
                errorMsg += errorMsg.length() > 0 ? ",analystMethod" : "analystMethod";
            }

            if (!analysisData.hasKey("meshType")) {
                errorMsg += errorMsg.length() > 0 ? ",meshType" : "meshType";
            }

            if (!analysisData.hasKey("meshSize")) {
                errorMsg += errorMsg.length() > 0 ? ",meshSize" : "meshSize";
            }

            if (!analysisData.hasKey("radius")) {
                errorMsg += errorMsg.length() > 0 ? ",radius" : "radius";
            }

            if (errorMsg.length() > 0) {
                promise.reject(new Error(errorMsg));
            } else {
                DensityAnalystOnline analystOnline = new DensityAnalystOnline();
                String ip = serverInfo.getString("ip");
                String port = serverInfo.getString("port");
                String userName = serverInfo.getString("userName");
                String password = serverInfo.getString("password");

                // 必填
                analystOnline.login(ip, port, userName, password);
                analystOnline.setDatasetSource(analysisData.getString("datasetName"));
                analystOnline.setAnalystMethod(analysisData.getInt("analystMethod"));
                analystOnline.setResolution(analysisData.getInt("meshSize"));
                analystOnline.setRadius(analysisData.getDouble("radius"));
                analystOnline.setMeshType(analysisData.getInt("meshType"));

                // 可选
                if (analysisData.hasKey("weight")) {
                    analystOnline.setWeight(analysisData.getString("weight"));

                    if (analysisData.hasKey("areaUnit")) {
                        analystOnline.setAreaUnit(analysisData.getString("areaUnit"));
                    }
                    if (analysisData.hasKey("meshSizeUnit")) {
                        analystOnline.setMeshSizeUnit(analysisData.getString("meshSizeUnit"));
                    }
                    if (analysisData.hasKey("radiusUnit")) {
                        analystOnline.setRadiusUnit(analysisData.getString("radiusUnit"));
                    }
                    if (analysisData.hasKey("bounds")) {
                        ReadableArray bounds = analysisData.getArray("bounds");
                        if (bounds.size() == 4) {
                            double left = bounds.getDouble(0);
                            double bottom = bounds.getDouble(1);
                            double right = bounds.getDouble(2);
                            double top = bounds.getDouble(3);
                            analystOnline.setBounds(new Rectangle2D(left, bottom, right, top));
                        }
                    }
                    if (analysisData.hasKey("rangeCount")) {
                        analystOnline.setRangeCount(analysisData.getInt("rangeCount") + "");
                    }
                    if (analysisData.hasKey("colorGradientType")) {
                        analystOnline.setColorGradientType(analysisData.getString("colorGradientType"));
                    }

                    analystOnline.execute();
                    analystOnline.addListener(new DistributeAnalystListener() {
                        @Override
                        public void onPostExecute(boolean bResult, ArrayList<String> datasources) {

                            WritableArray arr = Arguments.createArray();
                            for (int i = 0; i < datasources.size(); i++) {
                                arr.pushString(datasources.get(i));
                            }

                            WritableMap data = Arguments.createMap();

                            data.putBoolean("result", bResult);
                            data.putArray("datasources", arr);

                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EventConst.ONLINE_ANALYST_RESULT, data);
                        }

                        @Override
                        public void onExecuteFailed(String errorInfo) {
                            WritableMap data = Arguments.createMap();

                            data.putBoolean("result", false);
                            data.putString("error", errorInfo);
                            context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                    .emit(EventConst.ONLINE_ANALYST_RESULT, data);

                        }
                    });
                    analystOnline.execute();

                    promise.resolve(true);
                }
            }


        } catch (Exception e) {
            promise.reject(e);
        }
    }

    /**
     * 在线分析-点聚合分析
     *
     * @param serverInfo
     * @param analysisData
     * @param promise
     */
    @ReactMethod
    public void aggregatePointsOnline(ReadableMap serverInfo, ReadableMap analysisData, Promise promise) {
        try {

            String errorMsg = "";
            if (!serverInfo.hasKey("ip")) {
                errorMsg += "ip";
            }

            if (!serverInfo.hasKey("port")) {
                errorMsg += "port";
            }

            if (!analysisData.hasKey("datasetName")) {
                errorMsg += "DatasetName";
            }

            if (!analysisData.hasKey("aggregateType")) {
                errorMsg += errorMsg.length() > 0 ? ",aggregateType" : "aggregateType";
            }

            if (!analysisData.hasKey("meshType")) {
                errorMsg += errorMsg.length() > 0 ? ",meshType" : "meshType";
            }

            if (!analysisData.hasKey("meshSize")) {
                errorMsg += errorMsg.length() > 0 ? ",meshSize" : "meshSize";
            }

            if (errorMsg.length() > 0) {
                promise.reject(new Error(errorMsg));
            } else {
                AggregatePointsOnline analystOnline = new AggregatePointsOnline();
                String ip = serverInfo.getString("ip");
                String port = serverInfo.getString("port");
                String userName = serverInfo.getString("userName");
                String password = serverInfo.getString("password");

                analystOnline.login(ip, port, userName, password);

                analystOnline.setDatasetSource(analysisData.getString("datasetName"));
                analystOnline.setAggregateType(analysisData.getString("aggregateType"));
                analystOnline.setResolution(analysisData.getInt("meshSize"));
                analystOnline.setMeshType(analysisData.getInt("meshType"));

                if (analysisData.hasKey("weight")) {
                    analystOnline.setWeight(analysisData.getString("weight"));
                }
//                if (analysisData.hasKey("areaUnit")) {
//                    analystOnline.setAreaUnit(analysisData.getString("areaUnit"));
//                }
                if (analysisData.hasKey("meshSizeUnit")) {
                    analystOnline.setMeshSizeUnit(analysisData.getString("meshSizeUnit"));
                }
//                if (analysisData.hasKey("radiusUnit")) {
//                    analystOnline.setRadiusUnit(analysisData.getString("radiusUnit"));
//                }
                if (analysisData.hasKey("bounds")) {
                    ReadableArray bounds = analysisData.getArray("bounds");
                    if (bounds.size() == 4) {
                        double left = bounds.getDouble(0);
                        double bottom = bounds.getDouble(1);
                        double right = bounds.getDouble(2);
                        double top = bounds.getDouble(3);
                        analystOnline.setBounds(new Rectangle2D(left, bottom, right, top));
                    }
                }
                if (analysisData.hasKey("numericPrecision")) {
                    analystOnline.setNumericPrecision(analysisData.getInt("numericPrecision"));
                }
                if (analysisData.hasKey("rangeCount")) {
                    analystOnline.setRangeCount(analysisData.getInt("rangeCount") + "");
                }
                if (analysisData.hasKey("regionDataset")) {
                    analystOnline.setRegionDataset(analysisData.getString("regionDataset"));
                }
                if (analysisData.hasKey("colorGradientType")) {
                    analystOnline.setColorGradientType(analysisData.getString("colorGradientType"));
                }
                if (analysisData.hasKey("statisticModes")) {
                    analystOnline.setStatisticModes(analysisData.getString("statisticModes"));
                }
                if (analysisData.hasKey("RangeMode")) {
                    analystOnline.setRangeMode(analysisData.getString("RangeMode"));
                }

                analystOnline.addListener(new DistributeAnalystListener() {
                    @Override
                    public void onPostExecute(boolean bResult, ArrayList<String> datasources) {

                        WritableArray arr = Arguments.createArray();
                        for (int i = 0; i < datasources.size(); i++) {
                            arr.pushString(datasources.get(i));
                        }

                        WritableMap data = Arguments.createMap();

                        data.putBoolean("result", bResult);
                        data.putArray("datasources", arr);

                        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(EventConst.ONLINE_ANALYST_RESULT, data);
                    }

                    @Override
                    public void onExecuteFailed(String errorInfo) {
                        WritableMap data = Arguments.createMap();

                        data.putBoolean("result", false);
                        data.putString("error", errorInfo);
                        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                                .emit(EventConst.ONLINE_ANALYST_RESULT, data);
                    }
                });
                analystOnline.execute();

                promise.resolve(true);
            }


        } catch (Exception e) {
            promise.reject(e);
        }
    }



//    class gestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            switch (longPressAction) {
//                case FACILITYANALYST:
//                    Point pt = new Point((int) e.getX(), (int) e.getY());
//                    SMap sMap = SMap.getInstance();
//                    MapControl mapControl = sMap.getSmMapWC().getMapControl();
//                    Selection selection = mapControl.getMap().getLayers().get(0).hitTestEx(pt, 20);
//                    if (selection != null && selection.getCount() > 0) {
//                        Recordset recordset = selection.toRecordset();
//                        GeoPoint point = (GeoPoint) recordset.getGeometry();
//                        m_elementIDs.add(recordset.getInt32("SMNODEID"));
//                        System.out.println(recordset.getInt32("SMNODEID"));
//                        GeoStyle geoStyle = getGeoStyle(new Size2D(10,
//                                10), new Color(255, 105, 0));
//                        geoStyle.setMarkerSymbolID(3614);
//                        point.setStyle(geoStyle);
//
//                        int count = m_elementIDs.size();
//                        TextPart textPart = new TextPart("要素" + count,
//                                new Point2D(point.getX(), point.getY()));
//                        GeoText geoText = new GeoText(textPart);
//                        TextStyle textStyle = new TextStyle();
//                        textStyle.setForeColor(new Color(0, 255, 0));
//                        geoText.setTextStyle(textStyle);
//
//                        mapControl.getMap().getTrackingLayer().add(point, "");
//                        mapControl.getMap().getTrackingLayer().add(geoText, "");
//                        mapControl.getMap().refresh();
//
//                        point.dispose();
//                        geoText.dispose();
//                        recordset.close();
//                        recordset.dispose();
//                    }
//                    break;
//                case TRANSPORTANALYST:
//                    break;
//            }
//        }
//    }
//
//    enum LongPressAction {
//        /**
//         * 空操作
//         */
//        NULL,
//        /**
//         *设施网络分析
//         */
//        FACILITYANALYST,
//        /**
//         *交通网络分析
//         */
//        TRANSPORTANALYST
//    }
}