package com.example.presentation.data.repositories.helpers.populators.simplecontact

import android.database.Cursor
import com.example.domain.entities.SimpleContactEntity

interface SimpleContactPopulator {
    fun populate(
        simpleContactEntity: SimpleContactEntity,
        cursor: Cursor
    ): SimpleContactEntity
}
