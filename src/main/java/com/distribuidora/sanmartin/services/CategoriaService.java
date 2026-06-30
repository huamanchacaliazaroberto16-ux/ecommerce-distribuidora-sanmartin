package com.distribuidora.sanmartin.services;

import com.distribuidora.sanmartin.models.Categoria;
import com.distribuidora.sanmartin.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public void guardar(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    public void eliminar(Integer id) {
        categoriaRepository.deleteById(id);
    }

    public Categoria obtenerPorId(Integer id) {
        return categoriaRepository.findById(id).orElse(null);
    }
}