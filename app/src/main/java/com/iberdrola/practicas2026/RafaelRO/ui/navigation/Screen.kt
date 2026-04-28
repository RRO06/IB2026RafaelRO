package com.iberdrola.practicas2026.RafaelRO.ui.navigation

sealed class Screen(val route : String) {
    object Home : Screen("home")
    object ListadoFacturas : Screen("listado_facturas")
    object Filtro : Screen("filtro")
    object Perfil : Screen("perfil")
    object FacturaElectronica : Screen("factura_electronica")
    object DetalleFactura : Screen("detalle_factura_listado/{facturaId}") {
        fun createRoute(id: Int) = "detalle_factura_listado/$id"
    }
    object DetalleFacturaActiva : Screen("detalle_factura/{contratoId}") {
        fun createRoute(id: Int) = "detalle_factura/$id"
    }
    object ActivarFactura : Screen("activar_factura/{contratoId}") {
        fun createRoute(id: Int) = "activar_factura/$id"
    }
    object ExitoActivacion : Screen("exito_activacion/{email}") {
        fun createRoute(email: String) = "exito_activacion/$email"
    }
    object ExitoModificacion : Screen("exito_modificacion/{email}") {
        fun createRoute(email: String) = "exito_modificacion/$email"
    }
    object ModificarEmail : Screen("modificar_email/{contratoId}"){
        fun createRoute(id: Int) = "modificar_email/$id"
    }
    object VerificarCodigo : Screen("verificar_codigo/{contratoId}"){
        fun createRoute(id: Int) = "verificar_codigo/$id"
    }
}