#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <queue>
#include <sstream>
#include <map>

using namespace std;

/* =====================================================
   APPOINTMENT LINKED LIST
   ===================================================== */

struct AppointmentRecord {
    int id;
    int patientId;
    int doctorId;
    string date;
    string slot;
    string status;
    AppointmentRecord* next;
};

class AppointmentRepository {
private:
    AppointmentRecord* head;
    int idSeed;

    AppointmentRecord* create(int pid, int did,
                              const string& date,
                              const string& slot) {
        return new AppointmentRecord{
            idSeed++, pid, did, date, slot,
            "SCHEDULED", nullptr
        };
    }

public:
    AppointmentRepository() : head(nullptr), idSeed(1) {}

    bool isAvailable(int doctorId,
                     const string& date,
                     const string& slot) const {
        AppointmentRecord* cur = head;
        while (cur) {
            if (cur->doctorId == doctorId &&
                cur->date == date &&
                cur->slot == slot &&
                cur->status == "SCHEDULED") {
                return false;
            }
            cur = cur->next;
        }
        return true;
    }

    void insert(int patientId,
                int doctorId,
                const string& date,
                const string& slot) {
        AppointmentRecord* rec =
            create(patientId, doctorId, date, slot);
        rec->next = head;
        head = rec;
    }

    void saveFile(const string& file) const {
        ofstream out(file);
        AppointmentRecord* cur = head;

        while (cur) {
            out << cur->id << ","
                << cur->patientId << ","
                << cur->doctorId << ","
                << cur->date << ","
                << cur->slot << ","
                << cur->status << "\n";
            cur = cur->next;
        }
    }

    void loadFile(const string& file) {
        ifstream in(file);
        if (!in.is_open()) return;

        string line;
        while (getline(in, line)) {
            stringstream ss(line);
            AppointmentRecord* rec = new AppointmentRecord();
            string temp;

            getline(ss, temp, ','); rec->id = stoi(temp);
            getline(ss, temp, ','); rec->patientId = stoi(temp);
            getline(ss, temp, ','); rec->doctorId = stoi(temp);
            getline(ss, rec->date, ',');
            getline(ss, rec->slot, ',');
            getline(ss, rec->status, ',');

            rec->next = head;
            head = rec;

            idSeed = max(idSeed, rec->id + 1);
        }
    }
};

AppointmentRepository appointmentRepo;

/* =====================================================
   SLOT GRAPH (BFS)
   ===================================================== */

struct SlotInfo {
    string time;
    bool available;
    int doctorId;
};

class SlotAvailabilityService {
private:
    map<string, vector<SlotInfo>> slotGraph;

    vector<string> dailySchedule() const {
        return {
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        };
    }

public:
    void setupDate(const string& date, int doctorId) {
        for (const string& t : dailySchedule()) {
            bool free =
                appointmentRepo.isAvailable(doctorId, date, t);
            slotGraph[date].push_back({ t, free, doctorId });
        }
    }

    vector<string> queryAvailable(const string& date, int doctorId) {
        if (!slotGraph.count(date)) {
            setupDate(date, doctorId);
        }

        vector<string> result;
        queue<SlotInfo> q;

        for (const auto& s : slotGraph[date]) {
            if (s.doctorId == doctorId)
                q.push(s);
        }

        while (!q.empty()) {
            SlotInfo cur = q.front();
            q.pop();

            if (cur.available)
                result.push_back(cur.time);
        }
        return result;
    }

    void lockSlot(const string& date,
                  const string& time,
                  int doctorId) {
        for (auto& s : slotGraph[date]) {
            if (s.time == time && s.doctorId == doctorId) {
                s.available = false;
                return;
            }
        }
    }
};

SlotAvailabilityService slotService;

/* =====================================================
   FILE-BASED CONTROLLERS (GUI SAFE)
   ===================================================== */

const string DATA_DIR = "../DataFiles/";

void executeScheduling() {
    ifstream in(DATA_DIR + "schedule_input.txt");
    string line;
    getline(in, line);

    stringstream ss(line);
    string temp, date, slot;
    int patientId, doctorId;

    getline(ss, temp, ','); patientId = stoi(temp);
    getline(ss, temp, ','); doctorId = stoi(temp);
    getline(ss, date, ',');
    getline(ss, slot, ',');

    ofstream out(DATA_DIR + "schedule_output.txt");

    if (appointmentRepo.isAvailable(doctorId, date, slot)) {
        appointmentRepo.insert(patientId, doctorId, date, slot);
        slotService.lockSlot(date, slot, doctorId);

        out << "SUCCESS\n";
        out << "Appointment booked: "
            << date << " " << slot << "\n";
    }
    else {
        out << "ERROR: Slot not available\n";
    }

    appointmentRepo.saveFile(DATA_DIR + "appointments.txt");
}

void executeSlotQuery() {
    ifstream in(DATA_DIR + "slots_query.txt");
    int doctorId;
    string date;

    in >> doctorId;
    in.ignore();
    getline(in, date);

    vector<string> slots =
        slotService.queryAvailable(date, doctorId);

    ofstream out(DATA_DIR + "available_slots.txt");
    out << "AVAILABLE_SLOTS\n";
    for (const auto& s : slots)
        out << s << "\n";
}

void executeExport() {
    appointmentRepo.saveFile(
        DATA_DIR + "all_appointments.txt"
    );
}

/* =====================================================
   APPLICATION ENTRY
   ===================================================== */

int main() {
    appointmentRepo.loadFile(
        DATA_DIR + "appointments.txt"
    );

    ifstream cmd(DATA_DIR + "schedule_command.txt");
    string command;
    getline(cmd, command);

    if (command == "SCHEDULE_APPOINTMENT")
        executeScheduling();
    else if (command == "GET_AVAILABLE_SLOTS")
        executeSlotQuery();
    else if (command == "GET_ALL_APPOINTMENTS")
        executeExport();

    return 0;
}
