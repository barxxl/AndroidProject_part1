package com.example.temple_run

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.temple_run.logic.gameManager
import com.example.temple_run.utilies.Constants
import com.example.temple_run.utilies.SignalManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton
    private lateinit var main_hearts_layout: Array<ImageView>
    private lateinit var obstaclesLayout: Array<Array<ImageView>>
    private lateinit var carLayout: Array<ImageView>
    private lateinit var obstaclePosition: MutableList<MutableList<Int>>
    private lateinit var gameManager: gameManager
    private var currentLane: Int = 1
    private var startTime: Long = 0
    private var timerOn: Boolean = false

    private var timerJob: Job?=null






    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            findViews()
            gameManager = gameManager(main_hearts_layout.size)
            initViews()
            refreshUI()

        //gameTimer = GameTimer(lifecycleScope)
    }
    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume - Starting timer")
        // Start the timer when the activity comes into the foreground
        startTimer()
    }
    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause - Stopping timer")
        // Stop the timer when the activity goes into the background
        stopTimer()
    }


    private fun findViews() {
        main_FAB_left = findViewById(R.id.main_FAB_left)
        main_FAB_right = findViewById(R.id.main_FAB_right)
        carLayout = arrayOf(
            findViewById(R.id.main_car0),
            findViewById(R.id.main_car1),
            findViewById(R.id.main_car2),
        )
        main_hearts_layout = arrayOf(
            findViewById(R.id.main_heart0),
            findViewById(R.id.main_heart1),
            findViewById(R.id.main_heart2)
        )
        obstaclesLayout = arrayOf(
            arrayOf(
                findViewById(R.id.main_obstacle_0_0),
                findViewById(R.id.main_obstacle_0_1),
                findViewById(R.id.main_obstacle_0_2)
            ),
            arrayOf(
                findViewById(R.id.main_obstacle_1_0),
                findViewById(R.id.main_obstacle_1_1),
                findViewById(R.id.main_obstacle_1_2)
            ),
            arrayOf(
                findViewById(R.id.main_obstacle_2_0),
                findViewById(R.id.main_obstacle_2_1),
                findViewById(R.id.main_obstacle_2_2)
            ),
            arrayOf(
                findViewById(R.id.main_obstacle_3_0),
                findViewById(R.id.main_obstacle_3_1),
                findViewById(R.id.main_obstacle_3_2)
            ),
            arrayOf(
                findViewById(R.id.main_obstacle_4_0),
                findViewById(R.id.main_obstacle_4_1),
                findViewById(R.id.main_obstacle_4_2)
            ),
            arrayOf(
                findViewById(R.id.main_obstacle_5_0),
                findViewById(R.id.main_obstacle_5_1),
                findViewById(R.id.main_obstacle_5_2)
            ),
            arrayOf(
                findViewById(R.id.main_obstacle_6_0),
                findViewById(R.id.main_obstacle_6_1),
                findViewById(R.id.main_obstacle_6_2)
            )
        )
        obstaclePosition =
            MutableList(obstaclesLayout.size) { MutableList(obstaclesLayout[0].size) { 0 } } // Initialize with zeros
    }

    private fun initViews() {
        main_FAB_left.setOnClickListener { view: View -> movementClicked(-1) }
        main_FAB_right.setOnClickListener { view: View -> movementClicked(1) }

    }

    private fun movementClicked(direction: Int) //direction=0 left, direction=1 right
    {
        currentLane = gameManager.movePlayer(currentLane, direction)
        refreshCarLayout()
    }

    private fun startTimer() {
        if (!timerOn && (timerJob == null || timerJob?.isActive == false)) {
            timerOn = true
            startTime = System.currentTimeMillis()
            timerJob = lifecycleScope.launch {
                Log.d("MainActivity", "Timer coroutine started")
                while (timerOn && isActive ) {
                    try {
                        // 1. Advance Game State based on time
                        val collisionOccurred = gameManager.moveObstacleGrid(currentLane, obstaclePosition, main_hearts_layout.size)

                        // 2. Update the entire UI
                        refreshUI()

                        // 3. Wait for the next interval
                        delay(Constants.Timer.DELAY)
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error in game loop: ${e.message}")
                        stopTimer()
                    }
                }

                timerOn = false
            }
        }
    }

    // Function to stop the timer coroutine
    private fun stopTimer() {
        timerOn = false // Signal the loop to stop
        timerJob?.cancel() // Cancel the coroutine
        timerJob = null // Clear the job reference
    }

    private fun refreshUI() {
        refreshObstacleLayout()
        refreshCarLayout()
        refreshHeartLayout()

    }
   private fun refreshObstacleLayout()
    {
        for(rowIndex in 0 until obstaclesLayout.size)
            for(colIndex in 0 until obstaclesLayout[0].size)
            {
                if(obstaclePosition[rowIndex][colIndex]==1)
                    obstaclesLayout[rowIndex][colIndex].visibility=View.VISIBLE
                else
                 obstaclesLayout[rowIndex][colIndex].visibility=View.INVISIBLE
            }


    }
   private fun refreshCarLayout()
    {
        for(i in 0 until carLayout.size)
            if(i==currentLane)
                carLayout[i].visibility=View.VISIBLE
            else
                carLayout[i].visibility=View.INVISIBLE
    }
   private fun refreshHeartLayout() {
       val index: Int = main_hearts_layout.size - 1
       for (i in 0..index)
       {
           if(i<gameManager.hits)
               main_hearts_layout[i].visibility=View.INVISIBLE
           else
               main_hearts_layout[i].visibility=View.VISIBLE
       }
       //endless game part
       if(gameManager.hits==main_hearts_layout.size)
       {

           for (i in 0..index)
               main_hearts_layout[i].visibility=View.VISIBLE
       }
   }


}