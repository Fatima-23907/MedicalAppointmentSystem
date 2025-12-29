#include <iostream>
#include <fstream>
#include <string>
#include <unordered_map>
#include <map>
#include <vector>
#include <set>
#include <sstream>
#include <algorithm>
#include <climits>

using namespace std;

/* =====================================================
   LEVEL 1: HASH TABLE (Fast Patient Lookup)
   ===================================================== */

struct Patient {
    int id;
    string name;
    int age;
    string disease;
    string phone;
    int priority; // 1 = High, 2 = Normal
};

unordered_map<int, Patient> patientTable;

/* =====================================================
   LEVEL 2: AVL TREE (Sorted Patient Storage)
   ===================================================== */

struct AVLNode {
    Patient data;
    AVLNode* left;
    AVLNode* right;
    int height;
};

class AVLTree {
private:
    AVLNode* root;

    int height(AVLNode* node) {
        return node ? node->height : 0;
    }

    int balanceFactor(AVLNode* node) {
        return node ? height(node->left) - height(node->right) : 0;
    }

    AVLNode* rotateRight(AVLNode* y) {
        AVLNode* x = y->left;
        AVLNode* T2 = x->right;

        x->right = y;
        y->left = T2;

        y->height = max(height(y->left), height(y->right)) + 1;
        x->height = max(height(x->left), height(x->right)) + 1;

        return x;
    }

    AVLNode* rotateLeft(AVLNode* x) {
        AVLNode* y = x->right;
        AVLNode* T2 = y->left;

        y->left = x;
        x->right = T2;

        x->height = max(height(x->left), height(x->right)) + 1;
        y->height = max(height(y->left), height(y->right)) + 1;

        return y;
    }

    AVLNode* insertNode(AVLNode* node, const Patient& patient) {
        if (!node) {
            return new AVLNode{ patient, nullptr, nullptr, 1 };
        }

        if (patient.id < node->data.id)
            node->left = insertNode(node->left, patient);
        else if (patient.id > node->data.id)
            node->right = insertNode(node->right, patient);
        else
            return node;

        node->height = 1 + max(height(node->left), height(node->right));
        int balance = balanceFactor(node);

        // Balancing cases
        if (balance > 1 && patient.id < node->left->data.id)
            return rotateRight(node);

        if (balance < -1 && patient.id > node->right->data.id)
            return rotateLeft(node);

        if (balance > 1 && patient.id > node->left->data.id) {
            node->left = rotateLeft(node->left);
            return rotateRight(node);
        }

        if (balance < -1 && patient.id < node->right->data.id) {
            node->right = rotateRight(node->right);
            return rotateLeft(node);
        }

        return node;
    }

    void inorderTraversal(AVLNode* node, ofstream& out) {
        if (!node) return;

        inorderTraversal(node->left, out);
        out << node->data.id << ","
            << node->data.name << ","
            << node->data.age << ","
            << node->data.disease << ","
            << node->data.phone << "\n";
        inorderTraversal(node->right, out);
    }

public:
    AVLTree() : root(nullptr) {}

    void insertPatient(const Patient& patient) {
        root = insertNode(root, patient);
    }

    void saveSorted(const string& filename) {
        ofstream out(filename);
        inorderTraversal(root, out);
        out.close();
    }
};

AVLTree patientAVL;

/* =====================================================
   LEVEL 3: GRAPH (Patient Referral Network)
   ===================================================== */

class PatientReferralGraph {
private:
    map<int, vector<pair<int, string>>> referrals;
    map<string, vector<int>> diseaseIndex;

public:
    void registerPatient(int id, const string& disease) {
        diseaseIndex[disease].push_back(id);
    }

    void addReferral(int from, int to, const string& reason) {
        referrals[from].push_back({ to, reason });
    }

    vector<int> getSimilarPatients(int id) {
        if (!patientTable.count(id)) return {};

        string disease = patientTable[id].disease;
        vector<int> result;

        for (int pid : diseaseIndex[disease]) {
            if (pid != id)
                result.push_back(pid);
        }
        return result;
    }

    int highestPriorityPatient(const string& disease) {
        int bestId = -1;
        int bestPriority = INT_MAX;

        for (int id : diseaseIndex[disease]) {
            if (patientTable[id].priority < bestPriority) {
                bestPriority = patientTable[id].priority;
                bestId = id;
            }
        }
        return bestId;
    }

    void exportAnalysis(const string& filename) {
        ofstream out(filename);

        out << "=== PATIENT REFERRAL ANALYSIS ===\n\n";

        for (auto& group : diseaseIndex) {
            out << "Disease: " << group.first << "\n";
            for (int id : group.second) {
                out << "  Patient ID: " << id
                    << " | Priority: " << patientTable[id].priority << "\n";
            }
            out << "\n";
        }

        for (auto& r : referrals) {
            out << "Patient " << r.first << " referrals:\n";
            for (auto& dest : r.second) {
                out << "  -> " << dest.first
                    << " (" << dest.second << ")\n";
            }
        }

        out.close();
    }
};

PatientReferralGraph referralGraph;

/* =====================================================
   FILE PATH CONFIG
   ===================================================== */

const string BASE_PATH =
"E:\\MedicalAppointmentSystem\\MedicalAppointmentSystem\\DataFiles\\";

/* =====================================================
   CORE OPERATIONS
   ===================================================== */

int calculatePriority(const Patient& p) {
    if (p.age > 60 || p.disease == "Emergency" || p.disease == "Critical")
        return 1;
    return 2;
}

void addPatientsFromFile() {
    ifstream in(BASE_PATH + "patient_input.txt");
    ofstream out(BASE_PATH + "patient_output.txt");

    if (!in.is_open()) {
        out << "ERROR: Input file not found\n";
        return;
    }

    string line;
    while (getline(in, line)) {
        Patient p;
        string temp;
        stringstream ss(line);

        getline(ss, temp, ','); p.id = stoi(temp);
        getline(ss, p.name, ',');
        getline(ss, temp, ','); p.age = stoi(temp);
        getline(ss, p.disease, ',');
        getline(ss, p.phone, ',');

        p.priority = calculatePriority(p);

        patientTable[p.id] = p;
        patientAVL.insertPatient(p);
        referralGraph.registerPatient(p.id, p.disease);
    }

    out << "SUCCESS: Patients added\n";
    out << "Total Records: " << patientTable.size() << "\n";
}

void searchPatientById() {
    ifstream in(BASE_PATH + "search_input.txt");
    ofstream out(BASE_PATH + "search_output.txt");

    int id;
    in >> id;

    if (!patientTable.count(id)) {
        out << "NOT_FOUND\n";
        return;
    }

    Patient p = patientTable[id];
    out << "FOUND\n";
    out << p.id << "," << p.name << "," << p.age << ","
        << p.disease << "," << p.phone << "\n";

    auto similar = referralGraph.getSimilarPatients(id);
    if (!similar.empty()) {
        out << "\nSimilar Patients:\n";
        for (int pid : similar)
            out << pid << " - " << patientTable[pid].name << "\n";
    }
}

void exportSortedPatients() {
    patientAVL.saveSorted(BASE_PATH + "sorted_patients.txt");
}

void exportHighPriorityPatients() {
    ofstream out(BASE_PATH + "priority_patients.txt");
    out << "=== HIGH PRIORITY PATIENTS ===\n\n";

    for (auto& p : patientTable) {
        if (p.second.priority == 1) {
            out << p.second.id << " | "
                << p.second.name << " | "
                << p.second.disease << "\n";
        }
    }
}

void analyzeNetwork() {
    referralGraph.addReferral(1, 2, "Specialist Visit");
    referralGraph.addReferral(2, 3, "Advanced Treatment");

    referralGraph.exportAnalysis(
        BASE_PATH + "patient_network_analysis.txt"
    );
}

/* =====================================================
   MAIN CONTROLLER
   ===================================================== */

int main() {
    ifstream cmd(BASE_PATH + "command.txt");
    string command;
    getline(cmd, command);

    if (command == "ADD_PATIENT") addPatientsFromFile();
    else if (command == "SEARCH_PATIENT") searchPatientById();
    else if (command == "GET_ALL_SORTED") exportSortedPatients();
    else if (command == "FIND_HIGH_PRIORITY") exportHighPriorityPatients();
    else if (command == "ANALYZE_NETWORK") analyzeNetwork();

    return 0;
}
