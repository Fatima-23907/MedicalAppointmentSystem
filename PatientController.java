#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <queue>
#include <sstream>
#include <map>

using namespace std;

/* =====================================================
   LINKED LIST: Appointment Records
   ===================================================== */

struct Appointment {
    int appointmentId;
    int patientId;
    int doctorId;
    string date;
    string timeSlot;
    string status;
    Appointment* next;
};

class AppointmentService {
private:
    Appointment* head;
    int idCounter;

    Appointment* buildAppointment(int pid, int did,
                                   const string& date,
                                   const string& slot) {
        return new Appointment{
            idCounter++, pid, did, date, slot,
            "SCHEDULED", nullptr
        };
    }

public:
    AppointmentService() : head(nullptr), idCounter(1) {}

    bool slotAvailable(int doctorId,
                       const string& date,
                       const string& slot) const {
        Appointment* curr = head;
        while (curr) {
            if (curr->doctorId == doctorId &&
                curr->date == date &&
                curr->timeSlot == slot &&
                curr->status == "SCHEDULED") {
                return false;
            }
            curr = curr->next;
        }
        return true;
    }

    void addAppointment(int patientId,
                        int doctorId,
                        const string& date,
                        const string& slot) {
        Appointment* appt =
            buildAppointment(patientId, doctorId, date, slot);
        appt->next = head;
        head = appt;
    }

    void persist(const string& file) const {
        ofstream out(file);
        Appointment* curr = head;

        while (curr) {
            out << curr->appointmentId << ","
                << curr->patientId << ","
                << curr->doctorId << ","
                << curr->date << ","
                << curr->timeSlot << ","
                << curr->status << "\n";
            curr = curr->next;
        }
    }

    void restore(const string& file) {
        ifstream in(file);
        if (!in.is_open()) return;

        string line;
        while (getline(in, line)) {
            stringstream ss(line);
            Appointment* appt = new Appointment();
            string temp;

            getline(ss, temp, ','); appt->appointmentId = stoi(temp);
            getline(ss, temp, ','); appt->patientId = stoi(temp);
            getline(ss, temp, ','); appt->doctorId = stoi(temp);
            getline(ss, appt->date, ',');
            getline(ss, appt->timeSlot, ',');
            getline(ss, appt->status, ',');

            appt->next = head;
            head = appt;

            idCounter = max(idCounter, appt->appointmentId + 1);
        }
    }
};

AppointmentService appointmentService;

/* =====================================================
   GRAPH (BFS): Doctor Slot Scheduling
   ===================================================== */

struct SlotNode {
    string time;
    bool free;
    int doctorId;
};

class SlotScheduler {
private:
    map<string, vector<SlotNode>> calendar;

    vector<string> dailySlots() const {
        return {
            "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00"
        };
    }

public:
    void initializeDate(const string& date, int doctorId) {
        for (const auto& t : dailySlots()) {
            bool available =
                appointmentService.slotAvailable(doctorId, date, t);
            calendar[date].push_back({ t, available, doctorId });
        }
    }

    vector<string> fetchAvailable(const string& date, int doctorId) {
        if (!calendar.count(date)) {
            initializeDate(date, doctorId);
        }

        vector<string> result;
        queue<SlotNode> bfs;

        for (const auto& s : calendar[date]) {
            if (s.doctorId == doctorId)
                bfs.push(s);
        }

        while (!bfs.empty()) {
            SlotNode current = bfs.front();
            bfs.pop();

            if (current.free)
                result.push_back(current.time);
        }
        return result;
    }

    void reserve(const string& date,
                 const string& time,
                 int doctorId) {
        for (auto& s : calendar[date]) {
            if (s.time == time && s.doctorId == doctorId) {
                s.free = false;
                return;
            }
        }
    }
};

SlotScheduler slotScheduler;

/* =====================================================
   FILE INTERFACE (GUI COMPATIBLE)
   ===================================================== */

const string DATA_DIR = "../DataFiles/";

void handleScheduling() {
    ifstream in(DATA_DIR + "schedule_input.txt");
    string row;
    getline(in, row);

    stringstream ss(row);
    string temp, date, slot;
    int patientId, doctorId;

    getline(ss, temp, ','); patientId = stoi(temp);
    getline(ss, temp, ','); doctorId = stoi(temp);
    getline(ss, date, ',');
    getline(ss, slot, ',');

    ofstream out(DATA_DIR + "schedule_output.txt");

    if (appointmentService.slotAvailable(doctorId, date, slot)) {
        appointmentService.addAppointment(patientId, doctorId, date, slot);
        slotScheduler.reserve(date, slot, doctorId);

        out << "SUCCESS\n";
        out << "Appointment scheduled on "
            << date << " at " << slot << "\n";
    }
    else {
        out << "ERROR: Slot already booked\n";
    }

    appointmentService.persist(DATA_DIR + "appointments.txt");
}

void handleSlotQuery() {
    ifstream in(DATA_DIR + "slots_query.txt");
    int doctorId;
    string date;

    in >> doctorId;
    in.ignore();
    getline(in, date);

    vector<string> slots =
        slotScheduler.fetchAvailable(date, doctorId);

    ofstream out(DATA_DIR + "available_slots.txt");
    out << "AVAILABLE_SLOTS\n";
    for (const auto& s : slots)
        out << s << "\n";
}

void handleExport() {
    appointmentService.persist(
        DATA_DIR + "all_appointments.txt"
    );
}

/* =====================================================
   PROGRAM CONTROLLER
   ===================================================== */

int main() {
    appointmentService.restore(
        DATA_DIR + "appointments.txt"
    );

    ifstream cmd(DATA_DIR + "schedule_command.txt");
    string command;
    getline(cmd, command);

    if (command == "SCHEDULE_APPOINTMENT")
        handleScheduling();
    else if (command == "GET_AVAILABLE_SLOTS")
        handleSlotQuery();
    else if (command == "GET_ALL_APPOINTMENTS")
        handleExport();

    return 0;
}
