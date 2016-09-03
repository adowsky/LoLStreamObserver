"use strict";

import React from 'react';
import localstorage from 'localStorage';
import AppView from "./AppView.jsx";
import Modal from "./elements/modal/Modal.jsx";
import AddEntryForm from "./elements/AddEntryForm.jsx";
import rest from './elements/Client';


export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.getFunctions = this.getFunctions.bind(this);
        this.removeStreamer = this.removeStreamer.bind(this);
        this.removeSummoner = this.removeSummoner.bind(this);
        this.addEntry = this.addEntry.bind(this);
        this.showAdd = this.showAdd.bind(this);
        this.refresh = this.refresh.bind(this);
        this.clearData = this.clearData.bind(this);
        this.fetchFromServer = this.fetchFromServer.bind(this);
        this.prepareRequestBody = this.prepareRequestBody.bind(this);
        this.onlineNotification = this.onlineNotification.bind(this);
        this.changeOptions = this.changeOptions.bind(this);
        this.offlineSummoners = this.offlineSummoners.bind(this);
        this.newSummonerInStreamer = this.newSummonerInStreamer.bind(this);

        console.log(this.props.host);

        this.state = {
            showAdd: false,
            invokeAdd: false,
        };

        this.rest = rest;
        this.summonerSound = new Audio("../sound/summoner_on.mp3");
        this.streamerSound = new Audio("../sound/streamer_on.mp3");
        this.summonerSound.volume = 0.5;
    }

    componentWillMount() {
        let streamersString = localstorage.getItem("streamers");
        let optionsString = localstorage.getItem("options");
        let streamers = (streamersString == null) ? [] : JSON.parse(streamersString);

        streamers = streamers.map((streamer) => {
            streamer.online = false;
            streamer.summoners = streamer.summoners.map(summoner => {
                if (summoner.hasOwnProperty('champion'))
                    delete summoner['champion'];
                summoner.online = false;
                return summoner;
            });
            return streamer;
        });

        let options = (optionsString == null) ? App.defaultOptions : JSON.parse(optionsString);
        let state = {
            streamers: streamers,
            options: (options instanceof Object) ? options : {}
        };

        this.setState(Object.assign(this.state, state));
    }

    componentDidMount() {
        let intervalId = setInterval(this.fetchFromServer, 180000);

        this.setState({interval: intervalId});
    }

    componentWillUnmount() {
        clearInterval(this.state.interval);
    }


    componentDidUpdate() {
        localstorage.setItem("streamers", JSON.stringify(this.state.streamers));
        localstorage.setItem("options", JSON.stringify(this.state.options));
    }

    getChildContext() {
        return {
            functions: this.getFunctions(),
            streamers: this.state.streamers,
            options: this.state.options,
            debug: true
        };
    }

    getFunctions() {
        return {
            addEntry: this.addEntry,
            showAdd: this.showAdd,
            refresh: this.refresh,
            purgeData: this.clearData,
            fetchData: this.fetchFromServer,
            removeSummoner: this.removeSummoner,
            removeStreamer: this.removeStreamer,
            playSound: this.onlineNotification,
            changeOptions: this.changeOptions,
            newSummonerInStreamer: this.newSummonerInStreamer
        }; //@todo replace with proper functions
    }

    changeOptions(options) {
        let newOptions = Object.assign({}, this.state.options, options);

        this.setState({options: newOptions});
    }

    newSummonerInStreamer(streamer) {
        let index = this.state.streamers.indexOf(streamer);
        let streamers = this.state.streamers;

        streamers[index].summoners.push(
            {
                name: "",
                server: "EUNE",
            });

        this.setState(Object.assign({}, this.state, {
            streamers: streamers
        }));
    }

    onlineNotification(type = "") {
        if (this.state.options.sound) {
            switch (type) {
                case "summoner":
                    if (this.summonerSound.paused || this.summonerSound.currentTime === 0) {
                        this.summonerSound.play();
                    }
                    break;

                case "streamer":
                    if (this.streamerSound.paused || this.streamerSound.currentTime === 0) {
                        this.streamerSound.play();
                    }
                    break;
            }
        }
    }

    removeSummoner(streamer, summoner) {
        let index = streamer.summoners.indexOf(summoner);
        if (index >= 0) {
            streamer.summoners.splice(index, 1);
            this.setState(this.state);
        }
    }

    removeStreamer(streamer) {
        let index = streamer.summoners.indexOf(streamer);
        if (index >= 0) {
            this.state.splice(index, 1);
            this.setState(this.state);
        }
    }

    addEntry(entry) {
        let streamers = this.state.streamers;
        if (entry.name.trim().length > 0) {
            streamers.push(entry);
        }
        this.setState({
            streamers: streamers,
            showAdd: false,
            invokeAdd: false
        });
    }

    showAdd() {
        this.setState({showAdd: !this.state.showAdd});
    }

    refresh() {
        this.forceUpdate();
    }

    invokeAdd() {
        this.setState({invokeAdd: !this.state.invokeAdd});
    }

    clearData() {
        localstorage.removeItem('streamers');
        this.setState({streamers: []});
    }

    prepareRequestBody() {
        return this.state.streamers.map((streamer) => {
            let summoners = streamer.summoners.map(summoner => {
                return {
                    summoner: summoner.name,
                    server: summoner.server
                }
            });
            return {
                twitchName: streamer.name,
                lolAcc: summoners
            }
        });
    }

    fetchFromServer() {
        let currentStreamers = this.state.streamers;
        let host = this.props.host;

        this.rest({
            path: `http://${host}/streamers`,
            entity: this.prepareRequestBody()
        }).then((response) => {
            this.offlineSummoners();
            response.entity.forEach((streamer) => {
                let target = currentStreamers.find((old, index) => old.name === streamer.streamer);
                target.online = streamer.online;

                streamer.summoners.forEach((summ) => {
                    let validSummoner = target.summoners.find(summoner => {
                        let responseName = summ.summonerName.toLowerCase().replace(" ", "");
                        let localName = summoner.name.toLowerCase().replace(" ", "");

                        return localName === responseName && summ.server.toUpperCase() === summoner.server.toUpperCase()
                    });

                    if (validSummoner != null) {
                        validSummoner.online = true;
                        validSummoner.champion = summ.championNameId;
                    }
                });
            });
            this.setState({
                streamers: currentStreamers
            });
        });
    }

    offlineSummoners() {
        this.state.streamers.forEach(streamer => {
            streamer.summoners.forEach(summoner => {
                summoner.online = false;
                delete summoner.champion;
            })
        });
    }

    render() {
        let showAddModal = this.state.showAdd;
        let modalTitle = "Add new entry";
        let finishModal = this.invokeAdd.bind(this);
        let shouldFinish = this.state.invokeAdd;
        let finishAdd = this.addEntry;

        return <AppView >
            {(showAddModal) ?
                <Modal header={modalTitle} finish={finishModal}>
                    <AddEntryForm shouldFinish={shouldFinish} finish={finishAdd}/>
                </Modal> : null
            }
        </AppView>
    }

}

App.defaultOptions = {
    sound: true
};

App.childContextTypes = {
    functions: React.PropTypes.object,
    streamers: React.PropTypes.array,
    options: React.PropTypes.object,
    debug: React.PropTypes.bool
};