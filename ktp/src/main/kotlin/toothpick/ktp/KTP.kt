/*
 * Copyright 2019 Stephane Nicolas
 * Copyright 2019 Daniel Molinero Reguera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package toothpick.ktp

import toothpick.InjectorImpl
import toothpick.Scope
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.ktp.delegate.DelegateNotifier

/**
 * Main Toothpick API entry point for Kotlin.
 */
class KTP : Toothpick() {

    companion object TP {
        val delegateNotifier = DelegateNotifier()

        init {
            injector = object : InjectorImpl() {
                override fun <T : Any> inject(obj: T, scope: Scope) =
                    if (delegateNotifier.hasDelegates(obj)) {
                        delegateNotifier.notifyDelegates(obj, scope)
                    } else {
                        super.inject(obj, scope)
                    }
            }
        }

        fun openScope(name: Any): Scope = Toothpick.openScope(name)

        fun openScope(name: Any, scopeConfig: Scope.ScopeConfig): Scope = Toothpick.openScope(name, scopeConfig)

        fun openScopes(vararg names: Any): Scope = Toothpick.openScopes(*names)

        fun closeScope(name: Any) = Toothpick.closeScope(name)

        fun isScopeOpen(name: Any) = Toothpick.isScopeOpen(name)

        fun setConfiguration(configuration: Configuration) = Toothpick.setConfiguration(configuration)
    }
}
