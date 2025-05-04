package com.example.temple_run.logic

import kotlin.random.Random
import com.example.temple_run.utilies.SignalManager

class gameManager(private val lifeCount: Int) {

     var hits: Int = 0
        private set

    fun movePlayer(currentLane: Int, direction: Int) :Int
    {
        if((currentLane==0&&direction==-1)||(currentLane==2&&direction==1))
            return currentLane
        return currentLane+direction
    }

    fun moveObstacleGrid(currentLane: Int, obstaclePosition: MutableList<MutableList<Int>>,hearts_layout_size:Int)
    {
        val num_rows=obstaclePosition.size
        val num_cols=obstaclePosition[0].size

        //detect collision
        if(obstaclePosition[num_rows-1][currentLane]==1)
        {
            hits++
            SignalManager.getInstance().toast("you've hit an obstacle")
            SignalManager.getInstance().vibrate()
        }
        if (hits == hearts_layout_size) {
            hits = 0
            SignalManager.getInstance().toast("Hearts replenished!")
        }
        for(rowIndex in num_rows-2 downTo 0) //move each obstacle one step forward
            for(colIndex in 0 until num_cols)
            {
                obstaclePosition[rowIndex+1][colIndex]=obstaclePosition[rowIndex][colIndex]
                obstaclePosition[rowIndex][colIndex]=0

            }
        //randomly add a new obstacle

            val randomCol = Random.nextInt(0, num_cols)
            obstaclePosition[0][randomCol] = 1




    }

}