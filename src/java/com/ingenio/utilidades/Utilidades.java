package com.ingenio.utilidades;

import com.ingenio.objetos.Paginacion;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class Utilidades {
    
    private final static Utilidades UTILIDADES = new Utilidades();
    private boolean logServerActivo = false;

    private Utilidades(){}

    public static Utilidades get(){
        return UTILIDADES;
    }

    public void irAPagina(String direccion, HttpServletRequest solicitud,
                            HttpServletResponse respuesta, ServletContext contexto)
            throws ServletException, IOException, IllegalStateException{
        direccion = respuesta.encodeURL(direccion);
        RequestDispatcher despachador = contexto.getRequestDispatcher(direccion);
        despachador.forward(solicitud, respuesta);
    }

    public boolean autenticado(HttpSession sesion)
            throws ServletException, IOException{
        if (sesion == null || sesion.getAttribute("credencial") == null){ /* No autenticado */
          return (false);
        }
        return (true);
    }

    /**
     * Devuelve falso o verdadero si se activó el log en el web.xml
     * @return true si está activado, false si no.
     */
    public boolean isLogServerActivo() {
        return logServerActivo;
    }

    public void setLogServerActivo(boolean activo){
        logServerActivo = activo;
    }
    
    public void generaLogServer(Logger logger, Level level, String mensaje, Object[] data) {
        if (isLogServerActivo()) {
            logger.log(level, mensaje, data);
        }
    }
    
    /**
     * Permite cambiar un String recibido en el servlet a un boolean
     * @param bool El String a cambiar
     * @param LOG El Log de la clase que lo generó
     * @return El boolean generado.
     */
    
    public boolean parseBoolean(String bool, Logger LOG){
        boolean respuesta = false;
        if(bool != null){
            respuesta = bool.equals("on");
        }
        return respuesta;
    }
    
    /**
     * Permite cambiar un String a un short.
     * @param cadena Cadena a cambiar
     * @param LOG Log de la Clase que lo generó
     * @param generarLog Identifica si se genera el log o no.
     * @return short El short generado.
     */
    public short parseShort(String cadena, Logger LOG, boolean generarLog){
        short datos = 0;
        try{
            datos = Short.parseShort(cadena);
        } catch (NumberFormatException nfe){
            if(generarLog){
                generaLogServer(LOG, Level.WARNING, "Error al hacer parse de un dato que no es numérico {0} en {1}", new Object[]{cadena, LOG.getName()});
            }
        }
        return datos;
    }
    
    public int parseInt(String cadena, Logger LOG, boolean generarLog){
        int datos = 0;
        try{
            datos = Integer.parseInt(cadena);
        } catch (NumberFormatException nfe){
            if(generarLog){
                generaLogServer(LOG, Level.WARNING, "Error al hacer parse de un dato que no es numérico {0} en {1}", new Object[]{cadena, LOG.getName()});
            }
        }
        return datos;
    }
    
    /**
     * Permite agregar datos string o nulos a la función toJSON de los objetos
     * @param sb El StringBuilder que está almacenando el resultado
     * @param dato El String que se evalua para agregar
     */
    public void appendJSON(StringBuilder sb, String dato){
        if(dato != null){
            sb.append("\"").append(dato).append("\"");
        } else {
            sb.append(dato);
        }
    }

    public Calendar parseFecha(String fecha, Logger LOG, boolean generarLog){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar fechaResultado = Calendar.getInstance();
        try {
            fechaResultado.setTime(sdf.parse(fecha));
        } catch (ParseException ex) {
            fechaResultado = null;
            if(generarLog){
                generaLogServer(LOG, Level.WARNING, "Error al hacer parse de una fecha no válida {0} en {1}", new Object[]{fecha, LOG.getName()});
            }
        }
        return fechaResultado;
    }
    
    public String paginacionJSON(Paginacion paginacion){
        StringBuilder sb = new StringBuilder();
        sb.append("{")
            .append("\"pagina\":")
            .append(paginacion.getPagina())
            .append(",")
            .append("\"limite\":")
            .append(paginacion.getLimite())
            .append(",")
            .append("\"columna_orden\":")
            .append(paginacion.getColumna_orden())
            .append(",")
            .append("\"tipo_orden\":\"")
            .append(paginacion.getTipo_orden())
            .append("\"")
          .append("}");
        return sb.toString();
    }

    public String respuestaJSON(byte tipo, String mensaje, String objeto){
        StringBuilder sb = new StringBuilder();
        sb.append("{\"tipo\":")
          .append(tipo)
          .append(", \"mensaje\": \"")
          .append(mensaje)
          .append("\", \"objeto\": ");
        if(objeto == null || objeto.length() == 0){
            sb.append("\"\"");
        } else {
            sb.append(objeto);
        }
        sb.append("}");
        return sb.toString();
    }
    
    public String respuestaJSON(byte tipo, String mensaje, String objeto, Paginacion paginacion){
        StringBuilder sb = new StringBuilder();
        sb.append(respuestaJSON(tipo, mensaje, objeto));
        sb.delete(sb.length()-1, sb.length());
        sb.append(",")
            .append("\"paginacion\":")
            .append(paginacionJSON(paginacion))
            .append("}");
        return sb.toString();
    }
}
