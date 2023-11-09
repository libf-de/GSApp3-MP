package de.xorg.gsapp.di

import de.xorg.gsapp.ui.tools.PlatformInterface
import de.xorg.gsapp.ui.tools.AndroidPlatformImpl
import org.koin.dsl.module

val androidModule = module {
    single<PlatformInterface> { AndroidPlatformImpl() }
    //TODO: Maybe move PushHelper here?
}