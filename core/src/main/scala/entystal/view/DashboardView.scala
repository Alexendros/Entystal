package entystal.view

import scalafx.scene.layout.VBox
import scalafx.scene.chart.{PieChart => SFXPieChart}
import scalafx.collections.ObservableBuffer
import javafx.scene.chart.{PieChart => JFXPieChart}
import scalafx.scene.Scene
import entystal.service.RegistroService
import zio.Runtime

/** Vista de panel de control con gráficos agregados */
class DashboardView(service: RegistroService)(implicit runtime: Runtime[Any]) {
  private val chartData = ObservableBuffer[JFXPieChart.Data]()

  private val chart = new SFXPieChart(chartData) {
    title = "Totales registrados"
  }

  val rootPane = new VBox {
    children = Seq(chart)
  }

  val scene = new Scene(400, 300) {
    root = DashboardView.this.rootPane
  }

  /** Actualiza los datos del gráfico obteniéndolos del servicio */
  def refresh(): Unit = {
    val (totA, totL, totI) = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(service.aggregateTotals()).getOrThrow()
    }
    chartData.setAll(
      new JFXPieChart.Data("Activos", totA.toDouble),
      new JFXPieChart.Data("Pasivos", totL.toDouble),
      new JFXPieChart.Data("Inversiones", totI.toDouble)
    )
  }

  refresh()
}
