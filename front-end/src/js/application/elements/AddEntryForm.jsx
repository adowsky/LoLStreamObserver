import React from 'react';
import ServerPicker from './ContentTable/components/ServerPicker.jsx';

export default class AddEntryForm extends React.Component {

    constructor(props) {
        super(props);
        this.handleStreamerEdit = this.handleStreamerEdit.bind(this);

        this.defaultServer = "EUNE";
    }

    componentWillMount() {
        this.setState({
            streamer: "",
            summoners: [],
            focus: -1
        });
    }

    componentDidMount() {
        this.refs.streamerIn.focus();

    }

    componentDidUpdate() {
        if (this.props.shouldFinish) {
            let streamer = this.state.streamer;
            let summoners = this.state.summoners.filter(
                (summoner) =>summoner.name != undefined && summoner.name.trim().length > 0);
            this.props.finish({
                name: streamer,
                online: false,
                summoners: summoners
            });
        }
    }

    handleStreamerEdit(event) {
        this.setState({streamer: event.target.value})
    }


    handleSummonerEdit(index, event) {
        let focus = -1;
        if(this.state.summoners[index] == undefined){
            this.state.summoners[index] = {server: this.defaultServer};
            focus = index;
        }
        this.state.summoners[index].name = event.target.value;
        this.setState({summoners: this.state.summoners, focus: focus});
    }

    handlerSummonerServerEdit(index, event) {
        if(this.state.summoners[index] == undefined){
            this.state.summoners[index] = {};
        }
        this.state.summoners[index].server = event.target.value;
        this.setState({summoners: this.state.summoners});
    }

    render() {
        let streamer = this.state.streamer || "";
        let summonersDisable = (streamer.length == 0);
        let summonersNumber = this.state.summoners.length;
        return <div className="form-content">
            <input placeholder="Streamer" ref="streamerIn" onChange={this.handleStreamerEdit} value={streamer}/>
            {this.state.summoners.map((summoner, index) => {
                let serverHandler = this.handlerSummonerServerEdit.bind(this, index);
                let nameHandler = this.handleSummonerEdit.bind(this, index);

                return <div key={"summoner" + index.toString()} className="form-row">
                    <ServerPicker value={summoner.server} handler={serverHandler} disabled={summonersDisable}/>
                    <input placeholder="Summoner" onChange={nameHandler} value={summoner.name || ""}
                           disabled={summonersDisable} autoFocus={index === this.state.focus}/>
                </div>
            })}
            <div key={"summoner" + summonersNumber.toString()} className="form-row">
                <ServerPicker value={"EUNE"} handler={this.handlerSummonerServerEdit.bind(this, summonersNumber)}
                              disabled={summonersDisable}/>
                <input placeholder="Summoner" onChange={this.handleSummonerEdit.bind(this, summonersNumber)} value={""}
                       disabled={summonersDisable}/>
            </div>
            <div className="info">{(summonersDisable) ? AddEntryForm.disabledMessage : null}</div>
        </div>
    }
}

AddEntryForm.contextTypes = {
    streamers: React.PropTypes.array.isRequired,
    functions: React.PropTypes.object.isRequired
};

AddEntryForm.disabledMessage = "Fill streamer to unlock!";

