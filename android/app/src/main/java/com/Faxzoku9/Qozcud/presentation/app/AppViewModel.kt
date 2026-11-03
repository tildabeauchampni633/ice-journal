package com.Faxzoku9.Qozcud.presentation.app

import androidx.lifecycle.ViewModel
import com.Faxzoku9.Qozcud.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    fun finishSplash() {
        if (_screen.value is Screen.Splash) {
            backStack.clear()
            _screen.value = Screen.Menu
        }
    }

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current == target) {
            return
        }
        if (current !is Screen.Splash) {
            backStack.add(current)
        }
        _screen.value = target
    }

    fun replaceTop(target: Screen) {
        if (_screen.value == target) {
            return
        }
        _screen.value = target
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
    }

    fun back(): Boolean {
        if (backStack.isEmpty()) {
            return false
        }
        val previous = backStack.removeAt(backStack.lastIndex)
        _screen.value = previous
        return true
    }
}
