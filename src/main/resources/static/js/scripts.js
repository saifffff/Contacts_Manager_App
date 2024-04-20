console.log("this is from scripts.js");

// sidebar toogle
const toggleSidebar = () => {
    if($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }else{
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

// search function
const search =()=>{
    // targeting search-input using jquery
    let query = $("#search-input").val();
    if(query == ""){
        $(".search_result").hide();
    }else{
        console.log(query);

        // sending request to server
        // we will request this url for search implemented in our searchController
        let url =`http://localhost:8080/search/${query}`;
        // we will be using fetch api, it would return a promise
        fetch(url).then((response)=>{
            return response.json();
        }).then((data)=>{
            // we have data
            console.log("data : ", data);
            // modify data to use in our app as html
            let text = `<div class='list-group'>`;
            data.forEach((contact) => {
                //console.log(contact);
                text += `<a href="/user/get-contact?contactId=${contact.cid}" 
                class='list-group-item list-group-action'> ${contact.name} </a>`;
            });
            text += `</div>`;

            // append in app
            $(".search_result").html(text);
            $(".search_result").show();
        });


        
    }
}