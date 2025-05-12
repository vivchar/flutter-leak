package com.example.myapplication

import android.content.Context
import androidx.fragment.app.Fragment
import io.flutter.FlutterInjector
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.RenderMode
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.FlutterEngineGroup
import io.flutter.embedding.engine.dart.DartExecutor

class FlutterEngineCreator(
	private val instanceId: String,
	private val entryPointName: String,
	private val delegate: FlutterDelegate
) {
	interface FlutterDelegate {
		fun getEngineGroup(): FlutterEngineGroup
		fun getApplication(): Context

	}

	private lateinit var flutterEngine: FlutterEngine

	private fun createFlutterEngine(): FlutterEngine {
		val engineGroup = delegate.getEngineGroup()
		val engine = engineGroup.createAndRunEngine(delegate.getApplication(), createDartEntryPoint())
		FlutterEngineCache.getInstance().put(entryPointName, engine)
		return engine
	}

	private fun createDartEntryPoint(): DartExecutor.DartEntrypoint {
		val flutterLoader = FlutterInjector.instance().flutterLoader()
		flutterLoader.startInitialization(delegate.getApplication())
		flutterLoader.ensureInitializationComplete(delegate.getApplication(), null)

		return DartExecutor.DartEntrypoint(
			flutterLoader.findAppBundlePath(),
			entryPointName
		)
	}

	fun connect() {
		var engine = FlutterEngineCache.getInstance().get(entryPointName)
		if (engine == null) {
			engine = createFlutterEngine()
		}

		flutterEngine = engine
	}

	fun disconnect() {
		FlutterEngineCache.getInstance().remove(entryPointName)
		flutterEngine.destroy()
	}

	fun createFragment(): Fragment {
		return FlutterFragment.withCachedEngine(entryPointName)
			.renderMode(RenderMode.texture)
			.build()
	}

	fun getBinaryMessenger() = flutterEngine.dartExecutor.binaryMessenger
}