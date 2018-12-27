/*********************************************************************************
 Copyright © SuperMap. All rights reserved.
 Author: tronzzb
 Description: 专题制图类
 **********************************************************************************/
import {
  NativeModules
} from 'react-native'
let SThemeCartography = NativeModules.SThemeCartography

/**
 * 新建单值专题图层
 * 
 * @param params (数据源的索引/数据源的别名、 数据集名称、 单值专题图字段表达式、 颜色表样式)
 */
createThemeUniqueMap = (params) => {
  try {
    return SThemeCartography.createThemeUniqueMap(params)
  } catch (e) {
    console.error(e)
  }
}

/**
 * 设置单值专题图的默认风格
 * 
 * @param params 显示风格
 * @param layerName 图层名称
 */
setThemeUniqueDefaultStyle = (params, layerName) => {
  try {
    return SThemeCartography.setThemeUniqueDefaultStyle(params, layerName)
  } catch (e) {
    console.error(e)
  }
}

/**
 * 设置单值专题图子项的显示风格
 * 
 * @param params 显示风格
 * @param layerName 图层名称
 * @param itemIndex 单值专题图子项索引
 */
setThemeUniqueItemStyle = (params, layerName, itemIndex) => {
  try {
    return SThemeCartography.setThemeUniqueItemStyle(params, layerName, itemIndex)
  } catch (error) {
    console.error(e)
  }
}

/**
 * 设置单值专题图字段表达式
 *
 * @param params 单值专题图字段表达式UniqueExpression / 图层名称LayerName / 图层索引LayerIndex
 */
setUniqueExpression = (params) => {
  try {
    return SThemeCartography.setUniqueExpression(params)
  } catch (error) {
    console.error(error)
  }
}

//修改单值专题图
modifyThemeUniqueMap = (params) => {
  try {
    return SThemeCartography.modifyThemeUniqueMap(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 获取单值专题图的默认风格
 *
 * @param layerName 专题图层名称
 */
getThemeUniqueDefaultStyle = (layerName) => {
  try {
    return SThemeCartography.getThemeUniqueDefaultStyle(layerName)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 获取单值专题图的字段表达式
 *
 * @param layerName 专题图层名称
 */
getUniqueExpression = (layerName) => {
  try {
    return SThemeCartography.getUniqueExpression(layerName)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 获取数据集中的字段
 * @param udbPath UDB在内存中路径
 * @param datasetName 数据集名称
 */
getThemeExpressByUdb = (udbPath, datasetName) => {
  try {
    return SThemeCartography.getThemeExpressByUdb(udbPath, datasetName)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 获取数据集中的字段
 * @param layerName 图层名称
 */
getThemeExpressByLayerName = (layerName) => {
  try {
    return SThemeCartography.getThemeExpressByLayerName(layerName)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 获取数据集中的字段
 * @param layerIndex 图层索引
 */
getThemeExpressByLayerIndex = (layerIndex) => {
  try {
    return SThemeCartography.getThemeExpressByLayerIndex(layerIndex)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 新建分段专题图层
 *
 * @param params(数据源的索引 / 数据源的别名 / 打开本地数据源、 数据集名称、 分段字段表达式、 分段模式、 分段参数、 颜色渐变模式)
 */
createThemeRangeMap = (params) => {
  try {
    return SThemeCartography.createThemeRangeMap(params)
  } catch (e) {
    console.error(e)
  }
}

/**
 * 设置分段专题图的分段字段表达式
 *
 * @param params 分段字段表达式 图层名称 图层索引
 */
setRangeExpression = (params) => {
  try {
    return SThemeCartography.setRangeExpression(params)
  } catch (error) {
    console.error(error)
  }
}

//修改分段专题图
modifyThemeRangeMap = (params) => {
  try {
    return SThemeCartography.modifyThemeRangeMap(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 新建统一标签专题图
 *
 * @param params 
 */
createUniformThemeLabelMap = (params) => {
  try {
    return SThemeCartography.createUniformThemeLabelMap(params)
  } catch (error) {
    console.error(error)
  }
}


/**
 * 设置统一标签专题图的表达式
 *
 * @param params 
 */
setUniformLabelExpression = (params) => {
  try {
    return SThemeCartography.setUniformLabelExpression(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 设置统一标签专题图的背景形状
 *
 * @param params 
 */
setUniformLabelBackShape = (params) => {
  try {
    return SThemeCartography.setUniformLabelBackShape(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 设置统一标签专题图的字体
 *
 * @param params 
 */
setUniformLabelFontName = (params) => {
  try {
    return SThemeCartography.setUniformLabelFontName(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 设置统一标签专题图的字号
 *
 * @param params 
 */
setUniformLabelFontSize = (params) => {
  try {
    return SThemeCartography.setUniformLabelFontSize(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 设置统一标签专题图的旋转角度
 *
 * @param params 
 */
setUniformLabelRotaion = (params) => {
  try {
    return SThemeCartography.setUniformLabelRotaion(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * 设置统一标签专题图的颜色
 *
 * @param params 
 */
setUniformLabelColor = (params) => {
  try {
    return SThemeCartography.setUniformLabelColor(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * @param params 
 */
getUniformLabelExpression = (params) => {
  try {
    return SThemeCartography.getUniformLabelExpression(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * @param params 
 */
getUniformLabelBackShape = (params) => {
  try {
    return SThemeCartography.getUniformLabelBackShape(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * @param params 
 */
getUniformLabelFontName = (params) => {
  try {
    return SThemeCartography.getUniformLabelFontName(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * @param params 
 */
getUniformLabelFontSize = (params) => {
  try {
    return SThemeCartography.getUniformLabelFontSize(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * @param params 
 */
getUniformLabelRotaion = (params) => {
  try {
    return SThemeCartography.getUniformLabelRotaion(params)
  } catch (error) {
    console.error(error)
  }
}

/**
 * @param params 
 */
getUniformLabelColor = (params) => {
  try {
    return SThemeCartography.getUniformLabelColor(params)
  } catch (error) {
    console.error(error)
  }
}

/**获取所有数据源中的所有数据集名称 */
getAllDatasetNames = () => {
  try {
    return SThemeCartography.getAllDatasetNames()
  } catch (error) {
    console.error(error)
  }
}

getThemeExpressByDatasetName = (datasourceName, datasetName) => {
  try {
    return SThemeCartography.getThemeExpressByDatasetName(datasourceName, datasetName)
  } catch (error) {
    console.error(error)
  }
}

/**设置单值专题图的颜色方案 */
setUniqueColorScheme = (params) => {
  try {
    return SThemeCartography.setUniqueColorScheme(params)
  } catch (error) {
    console.error(error)
  }
}

/**设置分段专题图的颜色方案 */
setRangeColorScheme = (params) => {
  try {
    return SThemeCartography.setRangeColorScheme(params)
  } catch (error) {
    console.error(error)
  }
}

getRangeExpression = (params) => {
  try {
    return SThemeCartography.getRangeExpression(params)
  } catch (error) {
    console.error(error)
  }
}

getRangeMode = (params) => {
  try {
    return SThemeCartography.getRangeMode(params)
  } catch (error) {
    console.error(error)
  }
}

getRangeCount = (params) => {
  try {
    return SThemeCartography.getRangeCount(params)
  } catch (error) {
    console.error(error)
  }
}

export default {
  //单值
  createThemeUniqueMap,
  setThemeUniqueDefaultStyle,
  setThemeUniqueItemStyle,
  setUniqueExpression,
  modifyThemeUniqueMap,
  getThemeUniqueDefaultStyle,
  getUniqueExpression,
  setUniqueColorScheme,
  //分段
  createThemeRangeMap,
  setRangeExpression,
  modifyThemeRangeMap,
  setRangeColorScheme,
  getRangeExpression,
  getRangeMode,
  getRangeCount,
  //统一标签
  createUniformThemeLabelMap,
  setUniformLabelExpression,
  setUniformLabelBackShape,
  setUniformLabelFontName,
  setUniformLabelFontSize,
  setUniformLabelRotaion,
  setUniformLabelColor,
  getUniformLabelExpression,
  getUniformLabelBackShape,
  getUniformLabelFontName,
  getUniformLabelFontSize,
  getUniformLabelRotaion,
  getUniformLabelColor,
  //其他
  getThemeExpressByUdb,
  getThemeExpressByLayerName,
  getThemeExpressByLayerIndex,
  getThemeExpressByDatasetName,
  getAllDatasetNames,
}