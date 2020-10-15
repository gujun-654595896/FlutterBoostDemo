import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef TextViewCreatedCallback = void Function(TextViewController controller);

// platformView是平台（android/ios）的原生view,
// 为了能让一些现有的 native 控件直接引用到 Flutter app 中，
// Flutter 团队提供了 AndroidView 、UIKitView 两个 widget 来满足需求，
// 其实 platform view 就是 AndroidView 和 UIKitView 的总称，允许将 native view 嵌入到了 flutter widget 体系中，
// 完成 Datr 代码对 native view 的控制。
class TextView extends StatefulWidget {
  const TextView({
    Key key,
    this.onTextViewCreated,
  }) : super(key: key);

  final TextViewCreatedCallback onTextViewCreated;

  @override
  State<StatefulWidget> createState() => _TextViewState();
}

class _TextViewState extends State<TextView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        // 在 native 中的唯一标识符，需要与 native 侧的值相同
        viewType: 'plugins.test/view',
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onTextViewCreated == null) {
      return;
    }
    widget.onTextViewCreated(TextViewController._(id));
  }
}

class TextViewController {
  TextViewController._(int id)
      : _channel = MethodChannel('plugins.felix.angelov/textview_$id');

  final MethodChannel _channel;

  Future<void> setText(String text) async {
    assert(text != null);
    return _channel.invokeMethod('setText', text);
  }
}
