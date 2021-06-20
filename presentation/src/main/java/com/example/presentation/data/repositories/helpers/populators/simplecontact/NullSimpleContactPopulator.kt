package com.example.presentation.data.repositories.helpers.populators.simplecontact

import android.database.Cursor
import com.example.domain.entities.SimpleContactEntity

class NullSimpleContactPopulator : SimpleContactPopulator {
    override fun populate(
        simpleContactEntity: SimpleContactEntity,
        cursor: Cursor
    ): SimpleContactEntity {
        return simpleContactEntity
    }
}
