package com.capstone.champ.configuration;

import com.capstone.champ.model.Diagnosis;
import com.capstone.champ.model.Medicine;
import com.capstone.champ.repository.DiagnosisRepository;
import com.capstone.champ.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MasterDataSeeder implements CommandLineRunner {

    private final DiagnosisRepository diagnosisRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public void run(String... args) {
        seedDiagnoses();
        seedMedicines();
    }

    private void seedDiagnoses() {
        List<String> diagnosisNames = List.of(
                "Fever",
                "Common Cold",
                "Viral Infection",
                "Headache",
                "Migraine",
                "Gastritis",
                "Food Poisoning",
                "Diabetes",
                "Hypertension",
                "Asthma",
                "Allergic Rhinitis",
                "Flu",
                "Sore Throat",
                "Cough",
                "Back Pain",
                "Stomach Pain",
                "Skin Allergy",
                "Urinary Tract Infection",
                "Anemia",
                "Vitamin D Deficiency"
        );

        List<Diagnosis> toSave = new ArrayList<>();
        for (String name : diagnosisNames) {
            if (!diagnosisRepository.existsByDiagnosisNameIgnoreCase(name)) {
                Diagnosis diagnosis = new Diagnosis();
                diagnosis.setDiagnosisName(name);
                diagnosis.setDiagnosisCode(name.replaceAll("[^A-Za-z0-9]", "_").toUpperCase());
                diagnosis.setDescription("Seeded default diagnosis record");
                toSave.add(diagnosis);
            }
        }

        if (!toSave.isEmpty()) {
            diagnosisRepository.saveAll(toSave);
        }
    }

    private void seedMedicines() {
        List<String> medicineNames = List.of(
                "Paracetamol",
                "Ibuprofen",
                "Cetirizine",
                "Amoxicillin",
                "Azithromycin",
                "Omeprazole",
                "Pantoprazole",
                "Ondansetron",
                "ORS",
                "Levocetirizine",
                "Montelukast",
                "Metformin",
                "Amlodipine",
                "Losartan",
                "Vitamin D3",
                "Calcium",
                "Ferrous Sulfate",
                "Diclofenac",
                "Dextromethorphan",
                "Ambroxol"
        );

        List<Medicine> toSave = new ArrayList<>();
        for (String name : medicineNames) {
            if (!medicineRepository.existsByMedicineNameIgnoreCase(name)) {
                Medicine medicine = new Medicine();
                medicine.setMedicineName(name);
                toSave.add(medicine);
            }
        }

        if (!toSave.isEmpty()) {
            medicineRepository.saveAll(toSave);
        }
    }
}
