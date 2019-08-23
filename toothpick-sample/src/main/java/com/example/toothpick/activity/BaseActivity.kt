package com.example.toothpick.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.toothpick.model.DummyDependency
import javax.inject.Inject

/**
 * Advanced version of the BackpackItemsActivity.
 *
 * In this example, the backpack is retained on configuration
 * changes as it belongs to the view model scope which follows
 * the lifecycle of view model instances: when an instance is
 * destroyed, and later recreated, the scope remains unchanged
 * and the backpack instance will be the same.
 */
abstract class BaseActivity<T> : SomeInterfaceWithGeneric<T>, AppCompatActivity() {

    @Inject
    lateinit var dummyDependency: DummyDependency
}
