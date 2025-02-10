package com.example.dragonsuite

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dragonsuite.service.PlatformConnectionTester
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var platformTester: PlatformConnectionTester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize platform tester
        platformTester = PlatformConnectionTester(this)
        
        // Create TextView programmatically
        resultTextView = TextView(this).apply {
            setPadding(16, 16, 16, 16)
        }
        setContentView(resultTextView)
        
        // Run connection tests
        lifecycleScope.launch {
            resultTextView.text = "Testing connections..."
            val results = platformTester.testAllConnections()
            resultTextView.text = platformTester.formatResults(results)
        }
    }
}
