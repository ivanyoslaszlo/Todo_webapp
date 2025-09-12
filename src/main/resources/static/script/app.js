


const bekuldesgomb = document.getElementById("bekuldes-gomb");
const feladatmezo = document.getElementById("feladat-mezo");
const listam = document.getElementById("jegyzetlista");


function jegyzet_listahoz_adas(noteText) {

    let elem = document.createElement("li");

    let checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    

    let span = document.createElement("span");
    span.textContent = noteText;

    let deleteBtn = document.createElement("button");
    deleteBtn.classList.add("torles-gomb");
    deleteBtn.innerHTML = '<i class="fa-solid fa-trash"></i>';

    // checkbox: kész jelölése
    checkbox.addEventListener("change", function () {
        span.classList.toggle("done", checkbox.checked);
    });

    // kuka: csak akkor törölhet, ha át van húzva
    deleteBtn.addEventListener("click", async function () {
        if (!span.classList.contains("done")) {
            // kis vizuális jelzés, hogy nem törölhető
            deleteBtn.classList.add("shake");
            setTimeout(() => deleteBtn.classList.remove("shake"), 500);
            return;
        }

        // tényleges törlés
        listam.removeChild(elem);
        try {
            await fetch("/note", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify([noteText])
            });
        } catch (error) {
            console.error("Hiba a törlésnél", error);
        }
    });

    elem.appendChild(checkbox);
    elem.appendChild(span);
    
    elem.appendChild(deleteBtn);
    listam.appendChild(elem);
}


async function jegyzetmentes() {
    const noteText = document.getElementById("feladat-mezo").value.trim();

    if (!noteText) {
        
        return;
    }

    try {
        const response = await fetch("/note", {
            method: "POST",
            headers: { "Content-Type": "text/plain" },
            body: noteText
        });

        await response.text();
        jegyzet_listahoz_adas(noteText);

        document.getElementById("feladat-mezo").value = ""; 
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




bekuldesgomb.addEventListener("click", jegyzetmentes);
feladatmezo.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();
        jegyzetmentes();
    }
});


jegyzetlekeres();

