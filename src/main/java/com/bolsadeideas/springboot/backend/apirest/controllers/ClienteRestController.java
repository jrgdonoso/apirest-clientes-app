package com.bolsadeideas.springboot.backend.apirest.controllers;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.services.IClienteService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "*"})
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@CrossOrigin(origins = { "http://localhost:4200/", "http://127.0.0.1:4200/"})
	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> show(@PathVariable Long id) {
		System.out.println("Pasa por aca!!"); 
		
		Map<String, Object> response=new  HashMap<>();
		Cliente cli;
		
	
		
		try {
			 cli= this.clienteService.findById(id);
			 System.out.println("Pasa por aca!!"); 
			 
		}catch( DataAccessException e) {
			 System.out.println("Pasa por acaaaa!!"); 
			response.put("mensaje", "Error al realizar la consulta a la base de datos");		
			response.put("error",e.getMessage()+" , "+e.getMostSpecificCause().getMessage());
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		 if( cli == null ) {
			 System.out.println("Pasa por aca!!");
				response.put("mensaje", "El cliente ID: "+id+" no existe");	
				return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
				
		 }
		 
		 return new ResponseEntity<Cliente>(cli, HttpStatus.OK);
	 
		
		
	}
	
	
	
	
	
	@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public  ResponseEntity<?> create(@Valid @RequestBody Cliente cli, BindingResult result ) {
		
		
		Cliente cliente;
		Map<String, Object> response=new HashMap<>();
		
		
		if( result.hasErrors() ) {
		
			/*List<String> errores=new ArrayList<>();
			for(FieldError err: result.getFieldErrors() ){
				errores.add("campo "+err.getField()+" : "+err.getDefaultMessage());
			}*/
			
			List<String> errores= result.getFieldErrors()
					.stream()
					.map(err-> "campo "+err.getField()+" : "+err.getDefaultMessage())
					.collect(Collectors.toList());
		
			response.put("errors", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
	
		
		//cli.setCreateAt(new Date()); //Esto se puede reemplazar por un prePersist desde la entidad. ver Cliente.java
		try {
			cliente= this.clienteService.save(cli);
			
		    response.put("Mensaje", "El cliente fue creado correctamente");
		    response.put("cliente", cliente);
			
			return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
		
		}catch(DataAccessException e){
		
			response.put("Mensaje", "Error al realizar consulta(insert) en DB");
			response.put("error", e.getMessage()+", "+e.getMostSpecificCause().getMessage());
		    return new ResponseEntity<Map<String,Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*@PostMapping("/clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public  Cliente create(@RequestBody Cliente cli) {
		cli.setCreateAt(new Date()); //Esto se puede reemplazar por un prePersist desde la entidad. ver Cliente.java
		return this.clienteService.save(cli);
	}*/
	
	
	
	
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cli,BindingResult result, @PathVariable Long id) {
		
		Cliente cliActual;
		Map<String, Object> response=new HashMap<>();
		
		
		if( result.hasErrors() ) {
			
			//Aprender de esta estructura con tream y map
			List<String> errores= result.getFieldErrors()
					.stream()
					.map(err-> "campo "+err.getField()+" : "+err.getDefaultMessage())
					.collect(Collectors.toList());
		
			response.put("errors", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		

		try {
			cliActual= this.clienteService.findById(id);
			
			if( cliActual == null ) {
				
				response.put("mensaje", "El cliente ID: "+id+" no existe");	
				return  new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
					
			 }
	
		}catch( DataAccessException e) {
			
			response.put("mensaje", "Error al realizar la consulta a la base de datos");		
			response.put("error",e.getMessage()+" , "+e.getMostSpecificCause().getMessage());
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		cliActual.setNombre(cli.getNombre());
		cliActual.setApellido(cli.getApellido());
		cliActual.setEmail(cli.getEmail());
	
		try {
			
			response.put("mensaje", "Se actualizo correctamente el cliente");		
			response.put("cliente", this.clienteService.save(cliActual));
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta (Update) a la base de datos");		
			response.put("error",e.getMessage()+" , "+e.getMostSpecificCause().getMessage());
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	/*@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente update(@RequestBody Cliente cli, @PathVariable Long id) {
		Cliente cliActual=this.clienteService.findById(id);
		cliActual.setNombre(cli.getNombre());
		cliActual.setApellido(cli.getApellido());
		cliActual.setEmail(cli.getEmail());
	
		
		return this.clienteService.save(cliActual);
		
	}*/
	
	
	
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response=new HashMap<>();
		
		try { 
			this.clienteService.delete(id);
			
			response.put("mensaje", "Cliente eliminado");		
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
			
		}catch(DataAccessException e){
			response.put("mensaje", "Error al realizar la consulta (Delete) a la base de datos");		
			response.put("error",e.getMessage()+" , "+e.getMostSpecificCause().getMessage());
		    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	/*@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Long id) {
		 this.clienteService.delete(id);
	}*/
	

}
