/*********************************************************************************
 Copyright © SuperMap. All rights reserved.
 Author: tronzzb
 Description: AR高精度采集
 **********************************************************************************/
import {
    NativeModules,
    Platform,
} from "react-native"

let SCollectSceneFormView = NativeModules.SCollectSceneFormView

startRecording = () => {
    try {
        return SCollectSceneFormView.startRecording()
    } catch (error) {
        console.error(error)
    }
}

stopRecording = () => {
    try {
        return SCollectSceneFormView.stopRecording()
    } catch (error) {
        console.error(error)
    }
}

onDestroy = () => {
    try {
        return SCollectSceneFormView.onDestroy()
    } catch (error) {
        console.error(error)
    }
}

setArSceneViewVisible = (isVisible) => {
    try {
        return SCollectSceneFormView.setArSceneViewVisible(isVisible)
    } catch (error) {
        console.error(error)
    }
}

initSceneFormView = (datasourceAlias, datasetName, datasetPointName, language, UDBpath) => {
    try {
        return SCollectSceneFormView.initSceneFormView(datasourceAlias, datasetName, datasetPointName, language, UDBpath)
    } catch (error) {
        console.error(error)
    }
}

saveData = (name) => {
    try {
        return SCollectSceneFormView.saveData(name)
    } catch (error) {
        console.error(error)
    }
}

saveGPSData = (name) => {
    try {
        return SCollectSceneFormView.saveGPSData(name)
    } catch (error) {
        console.error(error)
    }
}

loadData = (index, isLine) => {
    try {
        return SCollectSceneFormView.loadData(index, isLine)
    } catch (error) {
        console.error(error)
    }
}

clearData = () => {
    try {
        return SCollectSceneFormView.clearData()
    } catch (error) {
        console.error(error)
    }
}

getHistoryData = () => {
    try {
        return SCollectSceneFormView.getHistoryData()
    } catch (error) {
        console.error(error)
    }
}

switchViewMode = () => {
    try {
        return SCollectSceneFormView.switchViewMode()
    } catch (error) {
        console.error(error)
    }
}

deleteData = (index) => {
    try {
        return SCollectSceneFormView.deleteData(index)
    } catch (error) {
        console.error(error)
    }
}

setDataSource = (name, path) => {
    try {
        return SCollectSceneFormView.setDataSource(name, path)
    } catch (error) {
        console.error(error)
    }
}

isShowTrace = () => {
    try {
        return SCollectSceneFormView.isShowTrace()
    } catch (error) {
        console.error(error)
    }
}

isLineDataset = (index) => {
    try {
        return SCollectSceneFormView.isLineDataset(index)
    } catch (error) {
        console.error(error)
    }
}

clearAllData = () => {
    try {
        return SCollectSceneFormView.clearAllData()
    } catch (error) {
        console.error(error)
    }
}

export default {
    startRecording,
    stopRecording,
    setArSceneViewVisible,
    onDestroy,
    initSceneFormView,
    saveData,
    saveGPSData,
    loadData,
    clearData,
    getHistoryData,
    switchViewMode,
    deleteData,
    setDataSource,
    isShowTrace,
    isLineDataset,
    clearAllData,
}