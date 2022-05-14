package com.velocityappsdj.zen

import com.velocityappsdj.zen.room.BatchTimeEntity
import junit.framework.TestCase
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class TimeUtilsTest  {

    @Test
    fun `different hours`() {
        assertThat(TimeUtils.sortTimeOfDay(
            mutableListOf(
                BatchTimeEntity("08:00pm", 20, 0,0),
                BatchTimeEntity("08:00am", 8, 0,0)
            )
        )).isEqualTo(
            mutableListOf(
                BatchTimeEntity("08:00am", 8, 0,0),
                BatchTimeEntity("08:00pm", 20, 0,0)
            )
        )
    }

    @Test
    fun `same hours`() {
        assertThat(TimeUtils.sortTimeOfDay(
            mutableListOf(
                BatchTimeEntity("08:24am", 8, 24,0),
                BatchTimeEntity("08:00am", 8, 0,0)
            )
        )).isEqualTo(
            mutableListOf(
                BatchTimeEntity("08:00am", 8, 0,0),
                BatchTimeEntity("08:24am", 8, 24,0)
            )
        )
    }




}