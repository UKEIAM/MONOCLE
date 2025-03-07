const Session = (function () {
  var getEpisodeId = function () {
    return JSON.parse(localStorage.getItem("episodeId") || "")
  }

  var setEpisodeId = function (id: string) {
    localStorage.setItem("episodeId", JSON.stringify(id))
  }

  var getPatientId = function () {
    return JSON.parse(localStorage.getItem("patientId") || "")
  }

  var setPatientId = function (id: string) {
    localStorage.setItem("patientId", JSON.stringify(id))
  }

  return {
    getEpisodeId: getEpisodeId,
    setEpisodeId: setEpisodeId,
    getPatientId: getPatientId,
    setPatientId: setPatientId,
  }
})()

export default Session
