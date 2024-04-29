package org.acme.models

import org.acme.entities.TestEntity
import org.acme.entities.NestedTestEntity

internal object TestEntityModel {
    fun basic(): TestEntity {
        return TestEntity(
            testEntityId = "testID",
            sortKey = "sort#key",
            nested =
            NestedTestEntity(
                nestedId = "nestedTestId"
            )
        )
    }
}
