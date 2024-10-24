package pe.edu.cibertec.thymeleaf.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import pe.edu.cibertec.web.bd.MySQLDataSource;
import pe.edu.cibertec.web.model.User;
import pe.edu.cibertec.web.repository.IRoleRepository;
import pe.edu.cibertec.web.service.UserService;

@Controller
public class FinalController {

public static Integer ses;
	
	@Autowired
	private IRoleRepository repos;
	
	@Autowired
	private UserService userService;
	
	@GetMapping({"/","/login"})
	public String login(Model model) {
		System.out.println("Mostrando login");
		User Unew = new User();
		model.addAttribute("userLogin", Unew);
		return "login";
	}
	@PostMapping("/login")
	public String login(@ModelAttribute User user, Model model) {
		System.out.println("Validando login");		
		String redirect = "login";
		User u = userService.validateUserByNameAndPassword(user.getEmail(), user.getPassword());
		if (u != null) {
			ses = 1;
			return "redirect:/listado";
		} else {
			model.addAttribute("errors", "Usuario o clave incorrectos");
			model.addAttribute("userLogin", new User());
		}
		return redirect;
	}
	
	@GetMapping("/logout")
	public String logout(Model model) {
		System.out.println("Cerrando sesión");
		model.addAttribute("userLogin", null);
		ses = null;
		return "redirect:/login";
	}
	
	@GetMapping("/listado")
	public String listado(Model model) {
		if(ses == null) {
			return "redirect:/login";
		} else {
			try {
				model.addAttribute("ltsUsuario", userService.consulta());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "listado";
		}
	}
	
	@GetMapping("/createUsuario")
	public String createUsuario(Model model) {
		model.addAttribute("crearUsu", new User());
		model.addAttribute("ltsRole", repos.findAll());
		return "createUsuario";
	}
	@PostMapping("/createUsuario")
	public String createUsuario(@ModelAttribute User user, Model model) {
		userService.guardarUsuario(user);
		model.addAttribute("msj", "Inserción realizada");
		model.addAttribute("crearUsu", new User());
		createUsuario(model);
		return "createUsuario";
	}
	
	@GetMapping("/updateUsuario/{iduser}")
	public String updateUsuario(@PathVariable  String iduser, Model model) {
		model.addAttribute("updUsu", new User());
		model.addAttribute("ltsRole", repos.findAll());
		User usu = userService.buscarUsuario(iduser);
		model.addAttribute("updUsu", usu);
		return "updateUsuario";
	}
	@PostMapping("/updateUsuario")
	public String updateUsuario(@ModelAttribute User user, Model model) {
		userService.guardarUsuario(user);
		return "redirect:/listado";
	}
	
	@GetMapping("/deleteUsuario/{iduser}")
	public String deleteRole(@PathVariable String iduser, Model model) {
		userService.eliminarUsuario(iduser);
		return "redirect:/listado";
	}
	
	@RequestMapping(value = "/usuarioReporte", method = RequestMethod.GET)
	public void personaReporte(HttpServletResponse response) throws JRException, IOException {
		InputStream jasperStream = this.getClass().getResourceAsStream("/reporte/Wood.jasper");
		System.out.println(jasperStream);
		Map<String, Object> params = new HashMap<String, Object>();
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, MySQLDataSource.getMySQLConnection());
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "inline; filename=usuario-report.pdf");
		final OutputStream outputStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	}
}
