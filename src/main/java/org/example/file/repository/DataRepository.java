package org.example.file.repository;

import org.example.file.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<FileUpload, Long> {

}
