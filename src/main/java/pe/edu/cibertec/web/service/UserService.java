package pe.edu.cibertec.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.edu.cibertec.web.model.User;
import pe.edu.cibertec.web.repository.IUserRepository;

@Service
public class UserService {
	
	@Autowired
	private IUserRepository userRepository;
	
	public User validateUserByNameAndPassword(String email, String password) {
		User u = userRepository.findByUserAndPassword(email, password);
		return u;
	}
	
	public User guardarUsuario(User usuario) {
		return userRepository.save(usuario);
	}
	
	public List<User> consulta() {
		List<User> listaRol = userRepository.consulta();
		return listaRol;
	}
	
	public List<User> listarUsuario() {
		List<User> listaRol = userRepository.findAll();
		return listaRol;
	}
	
	public User buscarUsuario(String id) {
		User rol = userRepository.getById(Integer.parseInt(id));
		return rol;
	}
	
	public void eliminarUsuario(String id) {
		this.userRepository.deleteById(Integer.parseInt(id));
	}
}
