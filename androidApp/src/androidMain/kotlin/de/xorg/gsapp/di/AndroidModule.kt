package de.xorg.gsapp.di

import de.xorg.gsapp.data.platform.PlatformInterface
import de.xorg.gsapp.ui.components.AndroidPlatformImpl
import org.koin.dsl.module

val androidModule = module {
    single<PlatformInterface> { AndroidPlatformImpl() }
    //TODO: Maybe move PushHelper here?
}