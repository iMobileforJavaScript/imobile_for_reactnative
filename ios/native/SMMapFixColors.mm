//
//  SMMapFixColors.m
//  Supermap
//
//  Created by wnmng on 2019/8/29.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "SMMapFixColors.h"
#import "SMap.h"
#import "SuperMap/ThemeUnique.h"
#import "SuperMap/ThemeRange.h"
#import "SuperMap/ThemeLabel.h"

@interface ColorHSB:NSObject{
    float _h;
    float _s;
    float _v;
}
@property (nonatomic,assign) float hue;
@property (nonatomic,assign) float saturation;
@property (nonatomic,assign) float brightness;
@end
@implementation ColorHSB


-(id)initWithH:(float)h s:(float)s b:(float)b{
    if (self = [super init]) {
        [self setHue:h];
        [self setSaturation:s];
        [self setBrightness:b];
    }
    return self;
}

-(id)initWithColor:(Color*)rgb{
    if (self = [super init]) {
        double r = ((double)rgb.red / 255.0);
        double g = ((double)rgb.green / 255.0);
        double b = ((double)rgb.blue / 255.0);
        
        double max = MAX(r, MAX(g, b));
        double min = MIN(r, MIN(g, b));
        
        float hue = 0.0;
        if (max == r && g >= b)
        {
            if (max - min == 0) hue = 0.0;
            else hue = 60 * (g - b) / (max - min);
        }
        else if (max == r && g < b)
        {
            hue = 60 * (g - b) / (max - min) + 360;
        }
        else if (max == g)
        {
            hue = 60 * (b - r) / (max - min) + 120;
        }
        else if (max == b)
        {
            hue = 60 * (r - g) / (max - min) + 240;
        }
        
        float sat = (max == 0) ? 0.0 : (1.0 - ((double)min / (double)max));
        float bri = max;
        _h = hue;
        _s = sat;
        _v = bri;
    }
    return self;
}

-(Color*)toColor{
    double r = 0;
    double g = 0;
    double b = 0;
    double hue = _h;
    double sat = _s;
    double bri = _v;
    if (sat == 0)
    {
        r = g = b = bri;
    }
    else
    {
        // the color wheel consists of 6 sectors. Figure out which sector you're in.
        double sectorPos = hue / 60.0;
        int sectorNumber = floor(sectorPos);
        // get the fractional part of the sector
        double fractionalSector = sectorPos - sectorNumber;
        
        // calculate values for the three axes of the color.
        double p = bri * (1.0 - sat);
        double q = bri * (1.0 - (sat * fractionalSector));
        double t = bri * (1.0 - (sat * (1 - fractionalSector)));
        
        // assign the fractional colors to r, g, and b based on the sector the angle is in.
        switch (sectorNumber)
        {
            case 0:
                r = bri;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = bri;
                b = p;
                break;
            case 2:
                r = p;
                g = bri;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = bri;
                break;
            case 4:
                r = t;
                g = p;
                b = bri;
                break;
            case 5:
                r = bri;
                g = p;
                b = q;
                break;
        }
    }
    int red = r * 255;
    int green = g * 255;
    int blue = b * 255; ;
    return [[Color alloc]initWithR:red G:green B:blue];
}

-(float)hue{
    return _h;
}
-(float)saturation{
    return _s;
}
-(float)brightness{
    return _v;
}
-(void)setHue:(float)hue{
    while (hue<0) {
        hue += 360;
    }
    while (hue>360) {
        hue -= 360;
    }
    _h = hue;
}
-(void)setSaturation:(float)value{
    while (value<0) {
        value+=1;
    }
    while (value>1) {
        value-=1;
    }
    _s = value;
}
-(void)setBrightness:(float)value{
    while (value<0) {
        value+=1;
    }
    while (value>1) {
        value-=1;
    }
    _v = value;
}

@end

@interface SMMapFixColors(){
    int _parame[12];
//    //Brightness
//    int _LB ;
//    int _FB ;
//    int _BB ;
//    int _TB ;
//    //Contrast
//    int _LC ;
//    int _FC ;
//    int _BC ;
//    int _TC ;
//    //Saturation
//    int _LS ;
//    int _FS ;
//    int _BS ;
//    int _TS ;
}

@end

@implementation SMMapFixColors

//由Objective-C的一些特性可以知道，在对象创建的时候，无论是alloc还是new，都会调用到 allocWithZone方法。
+(id)allocWithZone:(struct _NSZone *)zone{
    return [SMMapFixColors sharedInstance];
}
// 在通过拷贝的时候创建对象时，会调用到-(id)copyWithZone:(NSZone *)zone，-(id)mutableCopyWithZone:(NSZone *)zone方法
-(id)copyWithZone:(NSZone *)zone{
    return [SMMapFixColors sharedInstance];
}
-(id)mutableCopyWithZone:(NSZone *)zone{
    return [SMMapFixColors sharedInstance];
}

// 串行队列的创建方法
//static dispatch_queue_t queue = dispatch_queue_create("net.bujige.testQueue", DISPATCH_QUEUE_SERIAL);
+(id)sharedInstance{
    // 为了隔离外部修改，放在方法内部，在设置成静态变量
    static id smMapFixColorsInstance = nil;
    static dispatch_once_t onceQueue;
    dispatch_once(&onceQueue, ^{
        if(smMapFixColorsInstance ==  nil){
            //不是使用alloc方法，而是调用[[super allocWithZone:NULL] init]
            //已经重载allocWithZone基本的对象分配方法，所以要借用父类（NSObject）的功能来帮助出处理底层内存分配的杂物
            smMapFixColorsInstance =  [[super allocWithZone:nil] init] ;
            [smMapFixColorsInstance onCreate];
            
        }
    });
    return (SMMapFixColors*)smMapFixColorsInstance;
}

-(void)onCreate{
    for (int i=0; i<12; i++) {
        _parame[i] = 0;
    }
    
//    //Brightness
//    _LB =0  ;
//    _FB =0  ;
//    _BB =0  ;
//    _TB =0  ;
//    //Contrast
//    _LC =0  ;
//    _FC =0  ;
//    _BC =0  ;
//    _TC =0  ;
//    //Saturation
//    _LS =0  ;
//    _FS =0  ;
//    _BS =0  ;
//    _TS =0  ;
}

-(int)getMapFixColorModeValue:(FixColorsMode)mode{
    return  _parame[mode];
}

-(void)updateMapFixColorsMode:(FixColorsMode)mode value:(int)value{
    if (value<-100 || value>100) {
        return ;
    }
    
    Map *map = [[[[SMap singletonInstance]smMapWC]mapControl]map];
    NSArray *arrLayers = [self getAllLayersFromMap:map];
    for (int i=0; i<arrLayers.count; i++) {
        Layer *layer = [arrLayers objectAtIndex:i];
        if (layer.visible && layer.dataset!=nil) {
            
            DatasetType datasetType = layer.dataset.datasetType;
            if (datasetType==POINT
                || datasetType==LINE
                || datasetType==REGION
                || datasetType==PointZ
                || datasetType==LineZ
                || datasetType==RegionZ
                //|| datasetType==LINEM
                //|| datasetType==MODEL
                || datasetType==TABULAR
                || datasetType==Network
                || datasetType==NETWORK3D
                || datasetType==CAD
                || datasetType==TEXT
                ){
                if (layer.theme==nil) {
                    [self setDatasetVectorSimplyColors:layer withMode:mode from:_parame[mode] to:value];
                }else{
                    [self setDatasetVectorThemeColors:layer withMode:mode from:_parame[mode] to:value];
                }
            }
            //else if (DatasetTypeUtilities.isGridDataset(layer.getDataset().getType())) {
            //}
            //else if (DatasetTypeUtilities.isImageDataset(layer.getDataset().getType())) {
            //}
        }
    }
    _parame[mode] = value;
    [map refresh];
}

-(void)setDatasetVectorSimplyColors:(Layer*)layer withMode:(FixColorsMode)mode from:(int)nSrc to:(int)nDes{
    DatasetType datasetType = layer.dataset.datasetType;
    if (datasetType==POINT
        || datasetType==PointZ
        || datasetType==LINE
        || datasetType==Network
        || datasetType==LineZ
        || datasetType==NETWORK3D
        ){
        Color *colorOrg = [[(LayerSettingVector*) layer.layerSetting geoStyle] getLineColor];
        if (colorOrg!=nil && (mode==FCM_LB||mode==FCM_LH||mode==FCM_LS)) {
            Color*  matchedColor = [self color:colorOrg withMode:mode from:nSrc to:nDes];
            if (matchedColor!=nil) {
                [[(LayerSettingVector*) layer.layerSetting geoStyle] setLineColor:matchedColor];
            }
        }
    }else if(datasetType==REGION
             || datasetType==RegionZ){
        Color *colorF = [[(LayerSettingVector*) layer.layerSetting geoStyle] getFillForeColor];
        if (colorF!=nil && (mode==FCM_FB||mode==FCM_FH||mode==FCM_FS)) {
            Color*  matchedColor = [self color:colorF withMode:mode from:nSrc to:nDes];
            if (matchedColor!=nil) {
                [[(LayerSettingVector*) layer.layerSetting geoStyle] setFillForeColor:matchedColor];
            }
        }
        
        Color *colorOrg = [[(LayerSettingVector*) layer.layerSetting geoStyle] getLineColor];
        if (colorOrg!=nil && (mode==FCM_BB||mode==FCM_BH||mode==FCM_BS)) {
            Color*  matchedColor = [self color:colorOrg withMode:mode from:nSrc to:nDes];
            if (matchedColor!=nil) {
                [[(LayerSettingVector*) layer.layerSetting geoStyle] setLineColor:matchedColor];
            }
        }
    }
}

-(void)setDatasetVectorThemeColors:(Layer*)layer withMode:(FixColorsMode)mode from:(int)nSrc to:(int)nDes{
    Theme *theme = layer.theme;
    if (theme.themeType == TT_Unique) {
        ThemeUnique* themeUnique = (ThemeUnique*)theme;
        DatasetType datasetType = layer.dataset.datasetType;
        int nType = 0;
        if (datasetType==POINT
            || datasetType==PointZ
            || datasetType==LINE
            || datasetType==Network
            || datasetType==LineZ
            || datasetType==NETWORK3D
            ){
            nType = 1;
        }
        else if(datasetType==REGION
                || datasetType==RegionZ){
            nType = 2;
        }
        for (int i=0; i<themeUnique.getCount; i++) {
            if (nType==1) {
                Color *colorOrg = [[[themeUnique getItem:i] mStyle]getLineColor];
                if (colorOrg!=nil && (mode==FCM_LB||mode==FCM_LH||mode==FCM_LS)) {
                    Color*  matchedColor = [self color:colorOrg withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil) {
                        [[[themeUnique getItem:i] mStyle]setLineColor:matchedColor];
                    }
                }
            }else if(nType==2){
                Color *colorF = [[[themeUnique getItem:i] mStyle]getFillForeColor];;
                if (colorF!=nil) {
                    Color*  matchedColor = [self color:colorF withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil && (mode==FCM_FB||mode==FCM_FH||mode==FCM_FS)) {
                        [[[themeUnique getItem:i] mStyle]setFillForeColor:matchedColor];;
                    }
                }
                
                Color *colorOrg = [[[themeUnique getItem:i] mStyle]getLineColor];
                if (colorOrg!=nil) {
                    Color*  matchedColor = [self color:colorOrg withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil && (mode==FCM_BB||mode==FCM_BH||mode==FCM_BS)) {
                        [[[themeUnique getItem:i] mStyle]setLineColor:matchedColor];
                    }
                }
            }
        }
        
    }
    else if(theme.themeType == TT_Range){
        ThemeRange *themeRange = (ThemeRange*)theme;
        DatasetType datasetType = layer.dataset.datasetType;
        int nType = 0;
        if (datasetType==POINT
            || datasetType==PointZ
            || datasetType==LINE
            || datasetType==Network
            || datasetType==LineZ
            || datasetType==NETWORK3D
            ){
            nType = 1;
        }
        else if(datasetType==REGION
                || datasetType==RegionZ){
            nType = 2;
        }
        for (int i=0; i<themeRange.getCount; i++) {
            if (nType==1) {
                Color *colorOrg = [[[themeRange getItem:i] mStyle]getLineColor];
                if (colorOrg!=nil && (mode==FCM_LB||mode==FCM_LH||mode==FCM_LS)) {
                    Color*  matchedColor = [self color:colorOrg withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil) {
                        [[[themeRange getItem:i] mStyle]setLineColor:matchedColor];
                    }
                }
            }else if(nType==2){
                Color *colorF = [[[themeRange getItem:i] mStyle]getFillForeColor];;
                if (colorF!=nil) {
                    Color*  matchedColor = [self color:colorF withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil && (mode==FCM_FB||mode==FCM_FH||mode==FCM_FS)) {
                        [[[themeRange getItem:i] mStyle]setFillForeColor:matchedColor];;
                    }
                }
                
                Color *colorOrg = [[[themeRange getItem:i] mStyle]getLineColor];
                if (colorOrg!=nil) {
                    Color*  matchedColor = [self color:colorOrg withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil && (mode==FCM_BB||mode==FCM_BH||mode==FCM_BS)) {
                        [[[themeRange getItem:i] mStyle]setLineColor:matchedColor];
                    }
                }
            }
        }
    }
    else if(theme.themeType == TT_Graph){
        
    }
    else if(theme.themeType == TT_GraduatedSymbol){
        
    }else if(theme.themeType == TT_DotDensity){
        
    }else if(theme.themeType == TT_label){
        ThemeLabel *themeLabel = (ThemeLabel*)theme;
        if (themeLabel.getUniqueCount>0) {
            
            for (int i=0; i<themeLabel.getUniqueCount; i++) {
                Color *colorF = [[[themeLabel getUniqueItem:i] textStyle]getForeColor];
                if (colorF!=nil&& (mode==FCM_TB||mode==FCM_TH||mode==FCM_TS)) {
                    Color*  matchedColor = [self color:colorF withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil) {
                        [[[themeLabel getUniqueItem:i] textStyle]setForeColor:matchedColor];
                    }
                }
                
                Color *colorB =  [[[themeLabel getUniqueItem:i] textStyle]getBackColor];
                if (colorB!=nil && (mode==FCM_TB||mode==FCM_TH||mode==FCM_TS)) {
                    Color*  matchedColor = [self color:colorB withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil) {
                        [[[themeLabel getUniqueItem:i] textStyle]setBackColor:matchedColor];
                    }
                }
            }
            
        }else if(themeLabel.getRangeCount>0){
            
            for (int i=0; i<themeLabel.getRangeCount; i++) {
                Color *colorF = [[[themeLabel getRangeItem:i] mTextStyle]getForeColor];
                if (colorF!=nil&& (mode==FCM_TB||mode==FCM_TH||mode==FCM_TS)) {
                    Color*  matchedColor = [self color:colorF withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil) {
                        [[[themeLabel getRangeItem:i] mTextStyle]setForeColor:matchedColor];
                    }
                }
                
                Color *colorB =  [[[themeLabel getRangeItem:i] mTextStyle]getBackColor];
                if (colorB!=nil && (mode==FCM_TB||mode==FCM_TH||mode==FCM_TS)) {
                    Color*  matchedColor = [self color:colorB withMode:mode from:nSrc to:nDes];
                    if (matchedColor!=nil) {
                        [[[themeLabel getRangeItem:i] mTextStyle]setBackColor:matchedColor];
                    }
                }
            }
            
        }else if(themeLabel.mUniformStyle!=nil){
            Color *colorF = [[themeLabel mUniformStyle]getForeColor];
            if (colorF!=nil && (mode==FCM_TB||mode==FCM_TH||mode==FCM_TS)) {
                Color*  matchedColor = [self color:colorF withMode:mode from:nSrc to:nDes];
                if (matchedColor!=nil) {
                    [[themeLabel mUniformStyle]setForeColor:matchedColor];
                }
            }
            
            Color *colorB =  [[themeLabel mUniformStyle]getBackColor];
            if (colorB!=nil && (mode==FCM_TB||mode==FCM_TH||mode==FCM_TS)) {
                Color*  matchedColor = [self color:colorB withMode:mode from:nSrc to:nDes];
                if (matchedColor!=nil) {
                    [[themeLabel mUniformStyle]setBackColor:matchedColor];
                }
            }
        }
        
    }
    
}

-(Color*)color:(Color*)srcColor withMode:(FixColorsMode)mode from:(int)nSrc to:(int)nDes{
    
    ColorHSB *hsb = [[ColorHSB alloc]initWithColor:srcColor];
    int nDistance = nDes - nSrc;
    
    if (mode==FCM_LH||mode==FCM_BH||mode==FCM_FH||mode==FCM_TH) {
        float tempH = hsb.hue;
        tempH = tempH + nDistance*1.8;
        [hsb setHue:tempH];
    }else if(mode==FCM_LS||mode==FCM_BS||mode==FCM_FS||mode==FCM_TS){
        float tempS = hsb.saturation;
        tempS = tempS + nDistance*0.005;
        [hsb setSaturation:tempS];
    }else if(mode==FCM_LB||mode==FCM_BB||mode==FCM_FB||mode==FCM_TB){
        float tempB = hsb.brightness;
        tempB = tempB + nDistance*0.005;
        [hsb setBrightness:tempB];
    }
    
    return [hsb toColor];
}

/**
 * 获取地图的所有子图层
 *
 * @DesktopJavaDocable enable
 */
-(NSArray*)getAllLayersFromMap:(Map*)map{
    NSMutableArray *arrRes = [[NSMutableArray alloc]init];
    for (int i=0; i<map.layers.getCount; i++) {
        Layer *layerTemp = [map.layers getLayerAtIndex:i];
        if ([layerTemp isKindOfClass:LayerGroup.class]) {
            [arrRes addObjectsFromArray:[self getAllLayersFromGroup:(LayerGroup *)layerTemp]];
        }else{
            [arrRes addObject:layerTemp];
        }
    }
    return arrRes;
}
-(NSArray*)getAllLayersFromGroup:(LayerGroup*)group{
    NSMutableArray *arrRes = [[NSMutableArray alloc]init];
    for (int i=0; i<group.getCount;i++) {
        Layer *layerTemp = [group getLayer:i];
        if ([layerTemp isKindOfClass:LayerGroup.class]) {
            [arrRes addObjectsFromArray:[self getAllLayersFromGroup:(LayerGroup *)layerTemp]];
        }else{
            [arrRes addObject:layerTemp];
        }
    }
    return arrRes;
}


@end