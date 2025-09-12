const gombBekuldes = document.getElementById("bekuldes");
const inputFeladat = document.getElementById("feladat");
const listam = document.getElementById("jegyzetlista");

function jegyzet_listahoz_adas(noteText) {
    let elem = document.createElement("li");

    let span = document.createElement("span");
    span.textContent = noteText;

    let checkbox = document.createElement("input");
    checkbox.type = "checkbox";

   
    checkbox.addEventListener("change", function () {
        span.classList.toggle("done", checkbox.checked);
    });

    elem.appendChild(span);
    elem.appendChild(checkbox);
    listam.appendChild(elem);
}

async function jegyzetmentes() {
    const noteText = document.getElementById("feladat").value.trim();

    if (!noteText) {
        alert("Kérlek, írj be egy feladatot!");
        return;
    }

    try {
        const response = await fetch("/note", {
            method: "POST",
            headers: { "Content-Type": "text/plain" },
            body: noteText
        });

        const message=await response.text();
        jegyzet_listahoz_adas(noteText);

        const now=Date().toLocaleString();
        console.log(message+" : "+noteText+" . "+now)


        document.getElementById("feladat").value = ""; 
    } catch (error) {
        alert("Hiba történt a mentés során!");
        console.error(error);
    }
}

function jegyzetlekeres() {
    fetch("/note")
        .then(response => response.json())
        .then(item => {
            item.forEach(note => {
                jegyzet_listahoz_adas(note);
            });
        });
}

function jegyzetorles() {
    let torles = document.getElementById("delete");

    torles.addEventListener("click", async function () {
        const items = listam.querySelectorAll("li");
        let torlendojegyzetek = [];

        items.forEach(item => {
            const checkbox = item.querySelector("input[type='checkbox']");
            const span = item.querySelector("span");
            if (checkbox.checked) {
                torlendojegyzetek.push(span.textContent);
                listam.removeChild(item);
            }
        });

        if (torlendojegyzetek.length > 0) {
            try {
                await fetch("/note", {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(torlendojegyzetek)
                });
            } catch (error) {
                alert("Hiba");
            }
        }
    });
}

function osszesKijeloles() {
    const gomb = document.getElementById("selectAll");

    gomb.addEventListener("click", function () {
        const checkboxes = listam.querySelectorAll("input[type='checkbox']");
        checkboxes.forEach(checkbox => {
            checkbox.checked = true;
            const span = checkbox.previousElementSibling;
            if (span) span.classList.add("done");
        });
    });
}

gombBekuldes.addEventListener("click", jegyzetmentes);
inputFeladat.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();
        jegyzetmentes();
    }
});

osszesKijeloles();
jegyzetlekeres();
jegyzetorles();
