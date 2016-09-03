"use strict";

import React from 'react';
import StandardStreamerView from './StandardStreamerView.jsx';
import EditStreamerView from './EditStreamerView.jsx';
import  ButtonGroup from '../ButtonGroup.jsx';

export default class Streamer extends React.Component {
    static contextTypes = {
        functions: React.PropTypes.object.isRequired
    }

    constructor(props) {
        super(props);
        this.state = {
            edited: false,
            played: false
        };
        this.handleEditName = this.handleEditName.bind(this);
        this.handleChangeEditState = this.handleChangeEditState.bind(this);
        this.handleRemove = this.handleRemove.bind(this);
        this.handleGoToTwitch = this.handleGoToTwitch.bind(this);
        this.getEditButtons = this.getEditButtons.bind(this);
        this.getStandardButtons = this.getStandardButtons.bind(this);
        this.handleAddNewSummoner = this.handleAddNewSummoner.bind(this);

    }

    componentWillReceiveProps(nextProps) {
        let online = nextProps.streamer.online;

        if (online && !this.state.played) {
            this.context.functions.playSound("streamer");
            this.setState({played: true});
        } else if (!online && this.state.played) {
            this.setState({played: false});
        }
    }


    handleEditName(event) {
        this.props.streamer.name = event.target.value;
        this.setState(this.state);
    }

    handleRemove() {
        this.props.removeHandler(this.props.streamer);
    }

    handleChangeEditState() {
        this.setState({edited: !this.state.edited});
    }

    handleGoToTwitch() {
        let url = Streamer.baseUrl + encodeURI(this.props.streamer.name);
        window.open(url, "_blank");
    }

    handleAddNewSummoner(){
        this.context.functions.newSummonerInStreamer(this.props.streamer);
    }

    getStandardButtons() {
        return [
            {
                name: "Twitch",
                aClass: "twitch-button",
                action: this.handleGoToTwitch
            },
            {
                name: "Edit",
                aClass: "edit-button",
                action: this.handleChangeEditState
            },
            {
                name: "Remove",
                aClass: "remove-button",
                action: this.handleRemove
            }
        ];
    }

    getEditButtons() {
        return [
            {
                name: "Apply",
                aClass: "apply-button",
                action: this.handleChangeEditState
            },
            {
                name: "Add",
                aClass: "add-button",
                action: this.handleAddNewSummoner
            },
            {
                name: "Remove",
                aClass: "remove-button",
                action: this.handleRemove
            }
        ];
    }

    render() {
        let name = this.props.streamer.name;
        let online = this.props.streamer.online;
        let editHandlers = {
            name: this.handleEditName
        };
        let style = (online) ? "col streamer online" : "col streamer";
        let buttons = (this.state.edited) ? this.getEditButtons() : this.getStandardButtons();
        return (
            <div className={style}>
                {
                    (this.state.edited) ?
                        <EditStreamerView key="edit" name={name} handlers={editHandlers}/> :
                        <StandardStreamerView key="standard" name={name}/>
                }

                <ButtonGroup key="buttons" buttons={buttons}/>
            </div>
        );
    }
}
Streamer.contextTypes = {
    functions: React.PropTypes.object.isRequired
};

Streamer.baseUrl = "http://www.twitch.tv/";