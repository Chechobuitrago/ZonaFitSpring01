package gm.zona_fit.controlador;

import java.util.List;


import org.slf4j.LoggerFactory;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gm.zona_fit.servicio.IClienteServicio;
import gm.zona_fit.modelo.Cliente;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;

@Component
@Data
@ViewScoped
public class IndexControlador {
    
    @Autowired
    IClienteServicio clienteServicio;
    private List<Cliente> clientes;
    private Cliente clienteSeleccionado;
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        this.clientes = this.clienteServicio.listarClientes();
        this.clientes.forEach(cliente -> logger.info(cliente.toString()));
    }

    public void agregarCliente(){
        this.clienteSeleccionado = new Cliente();
    }

    public void guardarCliente(){
        logger.info("Cliente a guardar: "+this.clienteSeleccionado);
        //Agregar
        if (this.clienteSeleccionado.getId() == null) {
            this.clienteServicio.guardarCliente(this.clienteSeleccionado);
            this.clientes.add(this.clienteSeleccionado);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage("Cliente agregado"));
        }
        // Modificar (update)
        else {
            this.clienteServicio.guardarCliente(this.clienteSeleccionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cliente Actualizado") );
        }
        //Ocultar ventana modal
        PrimeFaces.current().executeScript("PF('ventanaModalCliente').hide()");
        // Actualizar tabla usando ajax
        PrimeFaces.current().ajax().update("forma-clientes:mensajes","forma-clientes:clientes-tabla");
        // Reset objeto de Cliente seleccionado
        this.clienteSeleccionado = null;
    }

    public void eliminarCliente(){
        logger.info("Cliente a eliminar "+this.clienteSeleccionado);
        this.clienteServicio.eliminarCliente(this.clienteSeleccionado);
        // Eliminar el registro de la lista de clientes
        this.clientes.remove(this.clienteSeleccionado);
        // Reset del objeto de cliente seleccionado
        this.clienteSeleccionado = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("cliente Eliminado"));
        PrimeFaces.current().ajax().update("forma-cliente:mensajes",
        "forma-clientes:clientes-tabla");
    }
}
