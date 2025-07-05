package entystal.view

import scalafx.scene.layout.VBox
import scalafx.scene.chart.{PieChart => SFXPieChart}
import scalafx.collections.ObservableBuffer
import javafx.scene.chart.{PieChart => JFXPieChart}
import scalafx.scene.Scene
import scalafx.application.Platform
import entystal.service.RegistroService
import entystal.i18n.I18n
import zio.Runtime

/** Vista de panel de control con gráficos agregados */
class DashboardView(service: RegistroService)(implicit runtime: Runtime[Any]) {
  private val chartData = ObservableBuffer[JFXPieChart.Data]()

  private val chart = new SFXPieChart(chartData) {
    title = I18n("chart.totales")
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
    Platform.runLater {
      chartData.setAll(
        new JFXPieChart.Data(I18n("chart.activos"), totA.toDouble),
        new JFXPieChart.Data(I18n("chart.pasivos"), totL.toDouble),
        new JFXPieChart.Data(I18n("chart.inversiones"), totI.toDouble)
      )
    }
  }

  refresh()
}
