package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class FlutterHostFragment : Fragment() {

	private lateinit var key: String

	private val flutterFragment: FlutterFragment by lazy {
		FlutterFragment
			.withCachedEngine(key)
			.build() as FlutterFragment
	}

	private val flutterEngine: FlutterEngine? get() = FlutterEngineCache.getInstance()[key]

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		key = System.currentTimeMillis().toString()
		key = "test_key"
		Log.d("FlutterHostFragment", "onCreate $key")
		prepareEngine()
	}

	private fun prepareEngine() {
		if (!FlutterEngineCache.getInstance().contains(key)) {
			FlutterEngine(requireContext()).apply {
				FlutterEngineCache.getInstance().put(key, this)
				dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_flutter_host, container, false)
		showFlutterFragment()
		Log.d("FlutterHostFragment", "onCreateView $key")
		return view
	}

	private fun showFlutterFragment() {
		parentFragmentManager
			.beginTransaction()
			.replace(R.id.flutter_container, flutterFragment)
			.commit()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		Log.d("FlutterHostFragment", "onViewCreated $key")

		view.findViewById<Button>(R.id.closeWrapper).setOnClickListener {
			findNavController().popBackStack()
		}

		view.findViewById<Button>(R.id.reOpenFlutter).setOnClickListener {
			showFlutterFragment()
		}

		flutterEngine?.dartExecutor?.binaryMessenger?.let {
			Log.d("FlutterHostFragment", "onViewCreated $it")
			MethodChannel(it, "com.example/my_channel")
				.setMethodCallHandler { call, _ ->
					if (call.method == "closeFlutter") {
						findNavController().popBackStack()
					}
				}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		Log.d("FlutterHostFragment", "onDestroyView $flutterEngine")
		flutterEngine?.destroy()
		FlutterEngineCache.getInstance().remove(key)
	}
}