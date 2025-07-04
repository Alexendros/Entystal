package entystal.gui

import org.testfx.framework.junit.ApplicationTest
import javafx.stage.Stage
import scala.jdk.CollectionConverters._
import entystal.view.MainView
import entystal.viewmodel.RegistroViewModel
import entystal.ledger.InMemoryLedger
import org.junit.Assert._
import zio.{Runtime, ZIO}

class MainViewSpec extends ApplicationTest {
  implicit val runtime: Runtime[Any] = Runtime.default

  override def start(stage: Stage): Unit = {
    val ledger = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ZIO.scoped(InMemoryLedger.live.build.map(_.get))).getOrThrow()
    }
    val vm     = new RegistroViewModel(ledger)
    val view   = new MainView(vm, ledger)
    stage.setScene(view.scene)
    stage.show()
  }

  @org.junit.Test
  def botonDeshabilitadoSinDatos(): Unit = {
    val boton = lookup("Registrar").queryButton()
    assertTrue(boton.isDisable)
  }

  @org.junit.Test
  def registrarActivo(): Unit = {
    val fields                                  = lookup(".text-field").queryAll().asScala.toSeq
    clickOn(fields.head).write("a1")
    clickOn(fields(1)).write("desc")
    val boton                                   = lookup("Registrar").queryButton()
    assertFalse(boton.isDisable)
    clickOn(boton)
    val nodes                                   = lookup(".label").queryAll().asScala.toSeq
    val labels: Seq[javafx.scene.control.Label] =
      nodes.collect { case l: javafx.scene.control.Label => l }
    assertTrue(labels.exists(_.getText == "Registro completado"))
  }
}
