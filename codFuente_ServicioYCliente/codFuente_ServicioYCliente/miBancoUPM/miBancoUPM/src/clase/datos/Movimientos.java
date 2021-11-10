package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "movimiento")
public class Movimientos {
	//ATRIBUTOS
		//private int id;
		private int id_cliente;
		private int id_movimiento;
		private String tipo; //tipo de movimiento
		private String fecha;
		private int id_cuenta;
		
		//CONSTRUCTOR
		public Movimientos(int id_cliente,int id_movimiento, String tipo, String fecha,int id_cuenta) { 
			this.id_cliente=id_cliente;
			this.id_movimiento=id_movimiento;
			this.tipo=tipo;
			this.id_cuenta=id_cuenta;
			this.fecha=fecha;
		}	
		
		public Movimientos() { 
			
		}
		
	//GETTERS Y SETTERS
		public int getIdCliente() {
			return this.id_cliente;
		}
		
		public void setIdCliente(int id_cliente) {
			this.id_cliente=id_cliente;
		}
		
		public int getIdCuenta() {
			return this.id_cuenta;
		}
		
		public void setIdCuenta(int id_cliente) {
			this.id_cuenta=id_cliente;
		}
		
		public int getIdMovimiento() {
			return this.id_movimiento;
		}
		
		public void setIdMovimiento(int id_movimiento) {
			this.id_movimiento=id_movimiento;
		}
		
		public String getTipo() {
			return this.tipo;
		}
		
		public void setTipo(String tipo) {
			this.tipo=tipo;
		}
		
		public String getFecha() {
			return this.fecha;
		}
		
		public void setFecha(String fecha) {
			this.fecha=fecha;
		}
		
	/*	@XmlAttribute(required=false)
		public int getId() {
			return this.id;
		}
		
		public void setId(int id) {
			this.id=id;
		}*/
		
}
