package com.boltive.integration

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.applovin.sdk.AppLovinSdk
import com.boltive.api.BoltiveMonitor
import com.boltive.integration.databinding.ActivityListBinding
import com.boltive.integration.example.Example
import com.boltive.integration.example.ExamplesRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var adapter: ArrayAdapter<Example>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)

        initViews()
        initApplovinMax()
    }

    private fun initViews() {
        supportActionBar?.title = "Boltive SDK v" + BoltiveMonitor.SDK_VERSION + " App"
        binding.apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_list_item_1,
                ExamplesRepository.getAll()
            )
            listView.adapter = adapter
            listView.setOnItemClickListener { _, _, position, _ ->
                val example = adapter.getItem(position) ?: return@setOnItemClickListener
                startActivity(
                    Intent(this@MainActivity, example.activity)
                )
            }
        }
    }

    private fun initApplovinMax() {
        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk { }
        AppLovinSdk.getInstance(this).settings.setVerboseLogging(false)
    }

}