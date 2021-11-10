package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movimientos")
public class MovimientoLink {
	private ArrayList<Link> movimientos;

	public MovimientoLink() {
		this.movimientos = new ArrayList<Link>();
	}

	@XmlElement(name="movimiento")
	public ArrayList<Link> getMovimientos() {
		return movimientos;
	}

	public void setMovimientos(ArrayList<Link> movimientos) {
		this.movimientos = movimientos;
	}
	
}
