package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;


@XmlRootElement(name = "cliente")
public class Cliente {
	//ATRIBUTOS
	private int id;
	private String nombre;
	private String apellido;
	private String DNI;
	private String domicilio;
	private String telefono;
	private ArrayList<Cuenta_Bancaria> Cuentas_Bancarias;
	private int saldo;
	private ArrayList<Movimientos> movimientos;
	
	//CONSTRUCTOR
	public Cliente(String nombre,String apellido,String domicilio,String telefono,String DNI) {
		this.nombre = nombre;
		this.apellido=apellido;
		this.DNI = DNI;
		this.telefono=telefono;
		this.domicilio = domicilio;
		this.Cuentas_Bancarias=new ArrayList<Cuenta_Bancaria>();
		this.movimientos=new ArrayList<Movimientos>();
	}

	
	public Cliente() {
		
	}

	@XmlAttribute(required=false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellido() {
		return apellido;
	}
	
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	public String getDNI() {
		return DNI;
	}
	
	public void setDNI(String DNI) {
		this.DNI = DNI;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public String getDomicilio() {
		return domicilio;
	}
	
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	
	@XmlElementWrapper(name="cuentas_bancarias")
    @XmlElement(name="cuenta_bancaria") 
    //@XmlTransient
    public ArrayList<Cuenta_Bancaria> getCuentas_Bancarias() {
        return Cuentas_Bancarias;
    }

    public void setCuentas_Bancarias(ArrayList<Cuenta_Bancaria> Cuentas_Bancarias) {
        this.Cuentas_Bancarias = Cuentas_Bancarias;
    }
    
  
    @XmlElement(name="movimientos") 
    //@XmlTransient
    public ArrayList<Movimientos> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(ArrayList<Movimientos> movimientos) {
        this.movimientos = movimientos;
    }
    
    public void añadirCuenta(Cuenta_Bancaria cuenta) {
    	this.Cuentas_Bancarias.add(cuenta);
    	this.saldo=this.saldo+cuenta.getSaldo();
    }
    
    public void añadirMovimiento(Movimientos movimiento) {
    	this.movimientos.add(movimiento);
    }
    
   
	
}
