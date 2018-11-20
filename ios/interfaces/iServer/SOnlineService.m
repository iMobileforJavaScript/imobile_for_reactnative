//
//  SOnlineService.m
//  Supermap
//
//  Created by lucd on 2018/11/6.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import "SOnlineService.h"
#import "Constants.h"
@interface SOnlineService(){
    OnlineService* m_onlineService;
}
@end
@implementation SOnlineService
static NSString* kTAG =  @"SOnlineService";;
#pragma mark -- 定义宏，让该类暴露给RN层
RCT_EXPORT_MODULE();
- (NSArray<NSString *> *)supportedEvents{
    return @[ONLINE_SERVICE_LOGIN,
             ONLINE_SERVICE_DOWNLOADFAILURE,
             ONLINE_SERVICE_DOWNLOADED,
             ONLINE_SERVICE_UPLOADED,
             ONLINE_SERVICE_UPLOADFAILURE,
             ONLINE_SERVICE_DOWNLOADING];
}

#pragma mark -- 定义宏的方法，让该类的方法暴露给RN层
#pragma mark ---------------------------- init
RCT_REMAP_METHOD(init, initWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    m_onlineService = [OnlineService sharedService];
    NSLog(@"SOnlineService init2");
}
#pragma mark ---------------------------- login
RCT_REMAP_METHOD(login, loginByUserName:(NSString *)userName password:(NSString *)password resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService loginWithUsername:userName password:password completionCallback:^(NSError *error) {
            if (error == nil) {
                NSLog(@"login success");
                NSNumber* number =[NSNumber numberWithBool:YES];
                resolve(number);
            } else {
                NSNumber* number =[NSNumber numberWithBool:NO];
                resolve(number);
            }
        }];
    } @catch (NSException *exception) {
        reject(kTAG, @"login failed", nil);
    }
}
#pragma mark ---------------------------- logout
RCT_REMAP_METHOD(logout,logoutWithResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService logoutWithCompletionCallback:^(NSError *error) {
            if (error == nil) {
                NSLog(@"logout success");
                NSNumber* number =[NSNumber numberWithBool:YES];
                resolve(number);
            } else {
                NSNumber* number =[NSNumber numberWithBool:NO];
                resolve(number);
            }
        }];
    } @catch (NSException *exception) {
        reject(kTAG, @"logout failed", nil);
    }
}
#pragma mark ---------------------------- publishService
RCT_REMAP_METHOD(publishService,dataName:(NSString*) dataName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService publishService:dataName completionHandler:^(BOOL result, NSString * _Nullable error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"publishService success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    } @catch (NSException *exception) {
        reject(kTAG, @"publishService failed", nil);
    }
}
#pragma mark ---------------------------- getDataList
RCT_REMAP_METHOD(getDataList,getDataListCurrentPage:(NSInteger)currentPage pageSize:(NSInteger)pageSize resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService getDataList:currentPage pageSize:pageSize completionHandler:^(NSString *dataJson, NSString *error) {
            if(error){
                reject(kTAG,error,nil);
            }else{
                resolve(dataJson);
            }
        }];
    } @catch (NSException *exception) {
        reject(kTAG, @"getDataList failed", nil);
    }
}
#pragma mark ---------------------------- getServiceList
RCT_REMAP_METHOD(getServiceList,getServiceListCurrentPage:(NSInteger)currentPage pageSize:(NSInteger)pageSize resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService getServiceList:currentPage pageSize:pageSize completionHandler:^(NSString *serviceJson, NSString *error) {
            if(error){
                reject(kTAG,error,nil);
            }else{
                resolve(serviceJson);
            }
        }];
    } @catch (NSException *exception) {
        reject(kTAG, @"getServiceList failed", nil);
    }
}
#pragma mark ---------------------------- registerWithEmail
RCT_REMAP_METHOD(registerWithEmail, registerWithEmail:(NSString*)email  nickname:(NSString*)nickname password:(NSString*)password resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService registerWithEmail:email nickname:nickname password:password completionHandler:^(BOOL result, NSString * _Nullable info) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"RegisterEmail success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"register failed",nil);
    }
}
#pragma mark ---------------------------- registerWithPhone
RCT_REMAP_METHOD(registerWithPhone,registerWithPhone:(NSString*)phoneNumber smsVerifyCode:(NSString*)smsVerifyCode nickname:(NSString*)nickname password:(NSString*)password resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService registerWithPhone:phoneNumber smsVerifyCode:smsVerifyCode nickname:nickname password:password completionHandler:^(BOOL result, NSString * _Nullable info) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"RegisterPhone success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"RegisterPhone failed",nil);
    }
}
#pragma mark ---------------------------- sendSMSVerifyCode
RCT_REMAP_METHOD(sendSMSVerifyCode,sendSMSVerifyCodePhoneNumber:(NSString*)phoneNumber resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService sendSMSVerifyCodeWithPhoneNumber:phoneNumber completionHandler:^(BOOL result, NSString * _Nullable info) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"sendSMSVerifyCode success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"sendSMSVerifyCode failed",nil);
    }
}

#pragma mark ---------------------------- download
RCT_REMAP_METHOD(download, downloadByPath:(NSString *)path fileName:(NSString *)fileName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        m_onlineService.downloadDelegate = self;
        
        if([[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:nil]){
            [[NSFileManager defaultManager] removeItemAtPath:path error:nil];
        }
        
        NSLog(@"start download");
        [m_onlineService downloadFileName:fileName filePath:path];
        
        NSNumber* number =[NSNumber numberWithBool:YES];
        resolve(number);
    } @catch (NSException *exception) {
        reject(kTAG, @"download failed", nil);
    }
}
#pragma mark ---------------------------- upload
RCT_REMAP_METHOD(upload, uploadByPath:(NSString *)path fileName:(NSString *)fileName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        m_onlineService.uploadDelegate = self;
        [m_onlineService uploadFilePath:path onlineFileName:fileName];
        
        NSNumber* number =[NSNumber numberWithBool:YES];
        resolve(number);
    } @catch (NSException *exception) {
        reject(kTAG, @"download failed", nil);
    }
}

#pragma mark ---------------------------- verifyCodeImage
RCT_REMAP_METHOD(verifyCodeImage,verifyCodeImageResolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService verifyCodeImage:^(UIImage * _Nullable verifyCodeImage, NSString *error) {
            NSData* data = UIImagePNGRepresentation(verifyCodeImage);
            NSString* pictruePath = [NSHomeDirectory() stringByAppendingString:@"/tmp/SuperMapCaches/vefityCodeImage.png"];
            if([[NSFileManager defaultManager] fileExistsAtPath:pictruePath] ){
                [[NSFileManager defaultManager] removeItemAtPath:pictruePath error:nil];
            }else{
                [[NSFileManager defaultManager] createDirectoryAtPath:[pictruePath stringByDeletingLastPathComponent] withIntermediateDirectories:YES attributes:nil error:nil];
            }
            [[NSFileManager defaultManager] createFileAtPath:pictruePath contents:data attributes:nil];
            resolve(pictruePath);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"verifyCodeImage failed",nil);
    }
}
#pragma mark ---------------------------- retrievePassword
RCT_REMAP_METHOD(retrievePassword,account:(NSString*)account verifyCodeImage:(NSString*)verifyCode isPhoneAccount:(BOOL)isPhoneAccount resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService retrievePassword:account verifyCodeImage:verifyCode isPhoneAccount:isPhoneAccount completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"retrievePassword success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"retrievePassword failed",nil);
    }
}
#pragma mark ---------------------------- retrievePasswordSecond
RCT_REMAP_METHOD(retrievePasswordSecond,firstResult:(BOOL)firstResult resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService retrievePasswordSecond:firstResult completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"retrievePasswordSecond success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"retrievePasswordSecond failed",nil);
    }
}
#pragma mark ---------------------------- retrievePasswordThrid
RCT_REMAP_METHOD(retrievePasswordThrid,secondResult:(BOOL)secondResult safeCode:(NSString*)safeCode resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService retrievePasswordThrid:secondResult safeCode:safeCode completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"retrievePasswordThrid success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"retrievePasswordThrid failed",nil);
    }
}
#pragma mark ---------------------------- retrievePasswordFourth
RCT_REMAP_METHOD(retrievePasswordFourth,thridResult:(BOOL)thridResult newPassword:(NSString*)newPassword resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService retrievePasswordFourth:thridResult newPassword:newPassword completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"retrievePasswordFourth success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"retrievePasswordFourth failed",nil);
    }
}
#pragma mark ---------------------------- deleteData
RCT_REMAP_METHOD(deleteData,deleteData:(NSString*)dataName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService deleteData:dataName completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"deleteData success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"deleteData failed",nil);
    }
}
#pragma mark ---------------------------- deleteService
RCT_REMAP_METHOD(deleteService,deleteService:(NSString*)dataName resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService deleteService:dataName completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"deleteService success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"deleteService failed",nil);
    }
}
#pragma mark ---------------------------- changeServiceVisibility
RCT_REMAP_METHOD(changeServiceVisibility,changeServiceVisibility:(NSString*)serviceName isPublic:(BOOL)isPublic resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try {
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService changeServiceVisibility:serviceName isPublic:isPublic completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"changeServiceVisibility success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    } @catch (NSException *exception) {
        reject(kTAG, @"changeServiceVisibility failed", nil);
    }
}
#pragma mark ---------------------------- changeDataVisibility
RCT_REMAP_METHOD(changeDataVisibility,changeDataVisibility:(NSString*)dataName isPublic:(BOOL)isPubilc resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService changeDataVisibility:dataName isPublic:isPubilc completionHandler:^(BOOL result, NSString *error) {
            NSNumber* number=nil;
            if(result){
                NSLog(@"changeDataVisibility success");
                number =[NSNumber numberWithBool:YES];
            }else{
                number =[NSNumber numberWithBool:NO];
            }
            resolve(number);
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"changeDataVisibility failed",nil);
    }
}
#pragma mark ---------------------------- getAllUserDataList
RCT_REMAP_METHOD(getAllUserDataList,getAllUserDataList:(NSInteger)currentPage resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService getAllUserDataList:currentPage completionHandler:^(NSString *dataJson, NSString *error) {
            if(error){
                reject(kTAG,error,nil);
            }else{
                resolve(dataJson);
            }
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"getAllUserDataList failed",nil);
    }
}
#pragma mark ---------------------------- getAllUserSymbolLibList
RCT_REMAP_METHOD(getAllUserSymbolLibList,getAllUserSymbolLibList:(NSInteger)currentPage resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject){
    @try{
//        OnlineService* m_onlineService = [OnlineService sharedService];
        [m_onlineService getAllUserSymbolLibList:currentPage completionHandler:^(NSString *dataJson, NSString *error) {
            if(error){
                reject(kTAG,error,nil);
            }else{
                resolve(dataJson);
            }
        }];
    }@catch(NSException* exception){
        reject(kTAG,@"getAllUserSymbolLibList failed",nil);
    }
}
# pragma mark ---------------------------- 下载协议
- (void)bytesWritten:(int64_t) bytesWritten totalBytesWritten:(int64_t) totalBytesWritten
totalBytesExpectedToWrite:(int64_t) totalBytesExpectedToWrite {
    @try {
        NSNumber* written = [NSNumber numberWithLongLong:totalBytesWritten];
        NSNumber* total = [NSNumber numberWithLongLong:totalBytesExpectedToWrite];
        float progress = [written floatValue] / [total floatValue] * 100;
        //        float progress = totalBytesWritten / totalBytesExpectedToWrite;
        NSLog(@"downloading: %f", progress);
        [self sendEventWithName:ONLINE_SERVICE_DOWNLOADING
                           body:@{
                                  @"progress": [NSNumber numberWithFloat:progress],
                                  @"downloaded": [NSNumber numberWithLongLong:totalBytesWritten],
                                  @"total": [NSNumber numberWithLongLong:totalBytesExpectedToWrite],
                                  }];
    } @catch (NSException *exception) {
        [self sendEventWithName:ONLINE_SERVICE_DOWNLOADFAILURE
                           body:exception.reason];
    }
}

- (void)downloadResult:(NSString*)error {
    @try {
        if (error != nil) {
            [self sendEventWithName:ONLINE_SERVICE_DOWNLOADFAILURE
                               body:error];
        } else {
            [self sendEventWithName:ONLINE_SERVICE_DOWNLOADED
                               body:[NSNumber numberWithBool:YES]];
        }
        
    } @catch (NSException *exception) {
        
    }
}

# pragma mark ---------------------------- 上传协议
- (void)uploadResult:(NSString*)error {
    @try {
        if (error != nil) {
            [self sendEventWithName:ONLINE_SERVICE_UPLOADED
                               body:error];
        } else {
            [self sendEventWithName:ONLINE_SERVICE_UPLOADFAILURE
                               body:[NSNumber numberWithBool:YES]];
        }
        
    } @catch (NSException *exception) {
        
    }
}
@end