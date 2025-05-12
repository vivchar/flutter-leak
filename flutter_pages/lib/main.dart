import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(home: BackButtonScreen());
  }
}

class BackButtonScreen extends StatelessWidget {
  const BackButtonScreen({super.key});

  static const MethodChannel _channel = MethodChannel('com.example/my_channel');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            _channel.invokeMethod('closeFlutter');
          },
          child: Text('Back'),
        ),
      ),
    );
  }
}
