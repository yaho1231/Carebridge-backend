package com.example.carebridge.controller;


import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.entity.Patient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Messages;
import retrofit2.http.DELETE;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    public StaffController() {

    }

    //더미데이터
    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Patient>> getPatients(@PathVariable int id) {
        List<Patient> patients = new ArrayList<>();
        List<ExaminationSchedule> examinationSchedules = new ArrayList<>();

        return ResponseEntity.ok(patients);
    }
    //더미데이터
    @GetMapping("/patient")
    public ResponseEntity<List<Patient>> getPatientLists() {
        List<Patient> patients = new ArrayList<>();
        List<ExaminationSchedule> examinationSchedules = new ArrayList<>();

        return ResponseEntity.ok(patients);
    }

    //더미데이터
    @PostMapping("/request/accept")
    public ResponseEntity<List<Messages>> acceptRequest(@RequestBody List<Messages> messages) {
        return ResponseEntity.ok(messages);
    }

    //더미데이터
    @PostMapping("/request/defer")
    public ResponseEntity<List<Messages>> deferRequest(@RequestBody List<Messages> messages) {
        return ResponseEntity.ok(messages);
    }

    //더미데이터
    @PostMapping("login")
    public ResponseEntity<List<MedicalStaff>> login(@RequestBody List<MedicalStaff> medicalStaffs) {
        return ResponseEntity.ok(medicalStaffs);
    }

    @PostMapping("macro")
    public ResponseEntity<Hospital> addMacro(@RequestBody Hospital hospital) {
        return null;
    }

    @GetMapping("macro/{id}")
    public ResponseEntity<Hospital> getMacro(@PathVariable int id) {
        return null;
    }

    @GetMapping("macro")
    public ResponseEntity<List<Hospital>> getMacroLists() {
        return null;
    }

    @DeleteMapping("macro/{id}")
    public ResponseEntity<Hospital> deleteMacro(@PathVariable int id) {
        return null;
    }

    @GetMapping("phraseHead/{id}")
    public ResponseEntity<Hospital> getPhraseHead(@PathVariable int id) {
        return null;
    }

    @PostMapping("phraseHead")
    public ResponseEntity<Hospital> addPhraseHead(@RequestBody Hospital hospital) {
        return null;
    }

    @GetMapping("phraseTail/{id}")
    public ResponseEntity<Hospital> getPhraseTail(@PathVariable String id) {
        return null;
    }

    @PostMapping("PhraseTail")
    public ResponseEntity<Hospital> addPhraseTail(@RequestBody Hospital hospital) {
        return null;
    }

    @GetMapping("quickList")
    public ResponseEntity<List<Hospital>> getQuickList() {
        return null;
    }

    @PostMapping("quickList")
    public ResponseEntity<Hospital> addQuickList(@RequestBody Hospital hospital) {
        return null;
    }

    @DeleteMapping("quickList/{id}")
    public ResponseEntity<Hospital> deleteQuickList(@PathVariable int id) {
        return null;
    }
}
