package com.example.demo.controller;


import com.example.demo.model.Account;
import com.example.demo.model.Album;
import com.example.demo.model.Photo;
import com.example.demo.payload.album.AlbumPayloadDTO;
import com.example.demo.payload.album.AlbumViewDTO;
import com.example.demo.service.AccountService;
import com.example.demo.service.AlbumService;
import com.example.demo.service.PhotoService;
import com.example.demo.util.AppUtils.AppUtil;
import com.example.demo.util.constants.AlbumError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Albumm Controller", description = "Controller for album and photo management")
@Slf4j
public class AlbumController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @PostMapping(value = "/albums/add",consumes = "application/json",produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400",description = "Please add valid name a description")
    @ApiResponse(responseCode = "201",description = "Account added")
    @Operation(summary = "Add an Album api")
    @SecurityRequirement(name="album-system-api")
    public ResponseEntity<AlbumViewDTO> addAlbum(@Valid @RequestBody AlbumPayloadDTO albumPayloadDTO, Authentication authentication){
        try{
            Album album=new Album();
            album.setName(albumPayloadDTO.getName());
            album.setDescription(albumPayloadDTO.getDescription());
            String email= authentication.getName();
            Optional<Account> accountOptional= accountService.findByEmail(email);
            Account account=accountOptional.get();
            album.setAccount(account);
            album=albumService.save(album);
            AlbumViewDTO albumViewDTO=new AlbumViewDTO(album.getId(), album.getName(),album.getDescription());
            return  ResponseEntity.ok(albumViewDTO);
        }catch (Exception e){
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString()+": "+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }
    @GetMapping(value = "/albums",produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "200",description = " List of albums")
    @ApiResponse(responseCode = "401",description = "Token missing")
    @ApiResponse(responseCode = "403",description = "Token error")
    @Operation(summary = "List of album api")
    @SecurityRequirement(name="album-system-api")
    public List<AlbumViewDTO> albums(Authentication authentication){
        String email=authentication.getName();
        Optional<Account> optionalAccount=accountService.findByEmail(email);
        Account account=optionalAccount.get();
        List<AlbumViewDTO>albums=new ArrayList<>();
        for(Album album:albumService.findByAccount_id(account.getId())){
            albums.add(new AlbumViewDTO(album.getId(),album.getName(),album.getDescription()));
        }
        return albums;


    }

    @PostMapping(value = "/albums/{album_id}/photoes",consumes = {"multipart/form-data"})
    @Operation(summary = "Upload photo album")
    @SecurityRequirement(name="album-system-api")
    public ResponseEntity<List<String>> photos(@RequestPart(required = true) MultipartFile[] files,@PathVariable long album_id, Authentication authentication){
       String email=authentication.getName();
       Optional<Account>optionalAccount=accountService.findByEmail(email);
       Account account=optionalAccount.get();
       Optional<Album>optionalAlbum=albumService.findById(album_id);
       Album album;
       if(optionalAlbum.isPresent()){
           album=optionalAlbum.get();
           if(account.getId()!=album.getId()){
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
           }
       }else{
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
       }


        List<String>fileNamesWithSuccess=new ArrayList<>();
        List<String>fileNamesWithError=new ArrayList<>();
//        Arrays.asList(files).stream().forEach(file->{
//            fileNames.add(file.getOriginalFilename());
//        });

        Arrays.asList(files).stream().forEach(file->{
        String contentType=file.getContentType();
        if(contentType.equals("image/png")
        ||contentType.equals("image/jpg")
        ||contentType.equals("image/jpeg")){
            fileNamesWithSuccess.add(file.getOriginalFilename());
            int length=10;
            boolean userLetters = true;
            boolean userNumbers = true;
            try{
                String fileName=file.getOriginalFilename();
                String generatedString= RandomStringUtils.random(length,userLetters,userNumbers);
                String final_photo_name=generatedString+fileName;
                String absolute_fileLocation= AppUtil.get_photo_upload_path(final_photo_name,album_id);
                Path path= Paths.get(absolute_fileLocation);
                Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
                Photo photo=new Photo();
                photo.setName(fileName);
                photo.setFileName(final_photo_name);
                photo.setOriginalFileName(fileName);
                photo.setAlbum(album);
                photoService.save(photo);


            }catch (Exception e){

            }
        }else{
            fileNamesWithError.add(file.getOriginalFilename());
        }
        });



        return ResponseEntity.ok(fileNamesWithSuccess);
    }


}

