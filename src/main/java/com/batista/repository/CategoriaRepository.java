package com.batista.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batista.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{


}
