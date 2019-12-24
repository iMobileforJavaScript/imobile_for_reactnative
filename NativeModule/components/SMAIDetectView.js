import * as React from 'react'
import {
  requireNativeComponent,
  ViewPropTypes,
  StyleSheet,
  View, InteractionManager, Platform,
  AppState,
  PanResponder,
} from "react-native";
import PropTypes from 'prop-types'
import {
  SAIDetectView,
} from 'imobile_for_reactnative'
import constants from "../../../../src/containers/workspace/constants"
import { TouchType } from '../../../../src/constants'

class SMAIDetectView extends React.Component {

  props: {
    language: String,
  }

  constructor() {
    super()

    this._panResponder = PanResponder.create({
      onStartShouldSetPanResponder: this._handleStartShouldSetPanResponder,
      onPanResponderGrant: this._handleMoveShouldSetPanResponder,
    })
    AppState.addEventListener('change', this.handleStateChange)
    this.stateChangeCount = 0
  }

  _handleStartShouldSetPanResponder = (evt, gestureState) => {
    return true;
  }

  _handleMoveShouldSetPanResponder = (evt, gestureState) => {
    switch (GLOBAL.TouchType) {
      case TouchType.NORMAL:
        break
      case TouchType.AIMAPTOUCH:
        // GLOBAL.AIMapSuspensionDialog.setVisible(false)
        // GLOBAL.AIFUNCTIONTOOLBAR.setVisible(true)
        // GLOBAL.SMMapSuspension.setVisible(true,{x:evt.nativeEvent.locationX,y:evt.nativeEvent.locationY})
        // GLOBAL.TouchType = TouchType.NORMAL
        break
    }
    return true;
  }



  state = {
    viewId: 0,
  }

  static propTypes = {
    onArObjectClick: PropTypes.func,
    ...ViewPropTypes,
  };

  componentDidUpdate(prevProps) {
    SAIDetectView.initAIDetect(this.props.language)
  //   SAIDetectView.startDetect()
  }

  componentDidMount() {
    SAIDetectView.setProjectionModeEnable(true)
  }

  componentWillUnmount() {
    SAIDetectView.dispose()
    AppState.removeEventListener('change', this.handleStateChange)
  }
  
  /************************** 处理状态变更 ***********************************/

  handleStateChange = async appState => {
    if (Platform.OS === 'android') {
      return
    }
    if (appState === 'inactive') {
      return
    }
    let count = this.stateChangeCount + 1
    this.stateChangeCount = count
    if (this.stateChangeCount !== count) {
      return
    } else if (this.prevAppstate === appState) {
      return
    } else {
      this.prevAppstate = appState
      this.stateChangeCount = 0
      if (appState === 'active') {
        SAIDetectView.startCamera()
      } else if (appState === 'background') {
        SAIDetectView.stopCamera()
      }
    }
  }

  _onArObjectClick = ({nativeEvent}) => {
    this.props.onArObjectClick && this.props.onArObjectClick(nativeEvent)
  }

  render() {
    var props = { ...this.props };

    return (
      <View
        style={styles.container}
        {...this._panResponder.panHandlers}
      >
        <RCTAIDetectView
          ref={ref => this.RCTAIDetectView = ref}
          {...props}
          style={styles.view}
          onArObjectClick={this._onArObjectClick}
        />
      </View>
    );
  }
}

var styles = StyleSheet.create({
  view: {
    flex: 1,
    alignSelf: 'stretch',
  },
  container: {
    position: 'absolute',
    top: 45,
    bottom: 0,
    left: 0,
    right: 0,
    // backgroundColor: '#rgba(255, 255, 255, 0)',
  },
});

var RCTAIDetectView = requireNativeComponent('RCTAIDetectView', SMAIDetectView)

export default SMAIDetectView