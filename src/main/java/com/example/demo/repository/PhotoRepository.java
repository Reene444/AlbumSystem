package com.example.demo.repository;

import com.example.demo.model.Album;
import com.example.demo.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo,Long> {
    List<Photo> findByAlbum_Id(long id);
}
