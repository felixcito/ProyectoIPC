package modelo;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Persona {
	
	private final StringProperty nombre = new SimpleStringProperty();
	private final StringProperty apellidos = new SimpleStringProperty();
        private String imagenPath;
		
	public Persona(String nombre, String apellidos,String imagenPath)
	{
		this.nombre.setValue(nombre);
		this.apellidos.setValue(apellidos);
                this.imagenPath = imagenPath;
	}
	
	public  StringProperty NombreProperty() {
		return this.nombre;
	}
	public String getNombre() {
		return this.NombreProperty().get();
	}
	public final void setNombre(String Nombre) {
		this.NombreProperty().set(Nombre);
	}
	public  StringProperty ApellidosProperty() {
		return this.apellidos;
	}
	public String getApellidos() {
		return this.ApellidosProperty().get();
	}
	public  void setApellidos(String Apellidos) {
		this.ApellidosProperty().set(Apellidos);
	}
        
//    private String imagenPath;
//
//    /**
//     * Get the value of imagenPath
//     *
//     * @return the value of imagenPath
//     */
       public String getImagenPath() {
           return imagenPath;
       }
//
//    /**
//     * Set the value of imagenPath
//     *
//     * @param imagenPath new value of imagenPath
//     */
        public void setImagenPath(String imagenPath) {
            this.imagenPath = imagenPath;
        }
//
//    public Persona(String nombre, String apellidos, String imagenPath) {
//        this.nombre.setValue(nombre);
//        this.apellidos.setValue(apellidos);
//        this.imagenPath = imagenPath;
//    }


}