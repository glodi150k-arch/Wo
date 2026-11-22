package com.nexus.panel

import android.net.VpnService

/**
 * Declares this app as a VPN-capable app so Android can display its
 * standard VPN consent screen. This demo service deliberately does not
 * establish a TUN interface or inspect/forward traffic.
 */
class LocalVpnConsentService : VpnService()
