package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cuentas_bancarias")
public class Cuentas_Bancarias {
	private ArrayList<Link> cuentas_bancarias;

	public Cuentas_Bancarias() {
		this.cuentas_bancarias = new ArrayList<Link>();
	}

	@XmlElement(name="cuenta_bancaria")
	public ArrayList<Link> getCuentas_Bancarias() {
		return cuentas_bancarias;
	}

	public void setCuentas_Bancarias(ArrayList<Link> cuentas_bancarias) {
		this.cuentas_bancarias = cuentas_bancarias;
	}
}
