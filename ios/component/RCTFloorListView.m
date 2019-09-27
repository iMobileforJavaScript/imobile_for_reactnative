//
//  RCTFloorListView.m
//  Supermap
//
//  Created by supermap on 2019/9/25.
//  Copyright © 2019年 Facebook. All rights reserved.
//

#import "RCTFloorListView.h"
#import <React/RCTBridge.h>
#import <React/RCTEventDispatcher.h>


@interface RCTFloorListView(ReactCategory)

//@property (nonatomic, copy) RCTBubblingEventBlock onSymbolClick;

@end

@implementation RCTFloorListView

RCT_EXPORT_MODULE(RCTFloorListView)

-(UIView *)view{
    MapControl *mapControl = [SMap singletonInstance].smMapWC.mapControl;
    FloorListView *view= [[FloorListView alloc]initWithFrame:CGRectMake(950, 300, 55, 200)];
    [view linkMapControl:mapControl];
    return view;
}

@end
