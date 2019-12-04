import {
  NativeModules,
  DeviceEventEmitter,
  NativeEventEmitter,
  Platform,
} from 'react-native'
import { EventConst } from '../../constains/index'
let Analyst = NativeModules.STransportationAnalyst

/**
 *
 * 加载设施网络分析模型
 * @param datasourceInfo
 * {
      alias,
      server,
      engineType,
 * }
 * @param setting
 * {
 *  networkDataset / networkLayer,
 *  weightFieldInfos: [ {name, ftWeightField, tfWeightField} ],
 *  barrierEdges: [],
 *  barrierNodes: [],
 *  directionField,
 *  edgeIDField,
 *  fNodeIDField,
 *  nodeIDField,
 *  tNodeIDField,
 *  tolerance,
 * }
 * @returns {Promise.<void>}
 */
async function load (datasourceInfo = {}, setting = {}) {
  return Analyst.load(datasourceInfo, setting)
}

/**
 * 设置障碍点
 * @param point
 * @param text
 * @returns {Promise}
 */
async function addBarrierNode (point, text = '') {
  return Analyst.addBarrierNode(point, text)
}

/**
 * 设置站点
 * @param point
 * @param text
 * @returns {Promise.<Promise|*>}
 */
async function addNode (point, text = '') {
  return Analyst.addNode(point, text)
}

/**
 * 设置起点
 * @param point
 * @param text
 * @returns {Promise.<Promise|Promise.<void>|Promise.<Promise|Promise.<void>>>}
 */
async function setStartPoint (point, text = '') {
  return Analyst.setStartPoint(point, text)
}

/**
 * 设置终点
 * @param point
 * @param text
 * @returns {Promise.<Promise|Promise.<Promise|Promise.<void>>|void>}
 */
async function setEndPoint (point, text = '') {
  return Analyst.setEndPoint(point, text )
}

async function clear () {
  return Analyst.clear()
}

/** 清除路线，保留点 **/
async function clearRoutes () {
  return Analyst.clearRoutes()
}

/**
 * 最佳路径分析
 * @param params
 * @param hasLeastEdgeCount 是否是最少弧段
 * @returns {Promise}
 */
async function findPath (params = {}, hasLeastEdgeCount = false) {
  return Analyst.findPath(params, hasLeastEdgeCount)
}

/**
 * 旅行商分析
 * @param params
 * @param isEndNodeAssigned 是否指定终点。为true则最后一个站点为终点
 * @returns {Promise.<*>}
 */
async function findTSPPath (params = {}, isEndNodeAssigned = false) {
  return Analyst.findTSPPath(params, isEndNodeAssigned)
}

async function undo () {
  return Analyst.undo()
}

async function redo () {
  return Analyst.redo()
}

export default {
  load,
  addBarrierNode,
  addNode,
  setStartPoint,
  setEndPoint,
  clear,
  clearRoutes,
  undo,
  redo,
  
  findPath,
  findTSPPath,
}