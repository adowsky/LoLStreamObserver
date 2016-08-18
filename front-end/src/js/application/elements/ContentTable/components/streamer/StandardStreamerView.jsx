import React from 'react';
import  ButtonGroup from '../ButtonGroup.jsx';

export default class StandardStreamerView extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        let name = this.props.name;
        return <div className="streamer-content">
            <span>{name}</span>
        </div>

    }
}

StandardStreamerView.propTypes = {
    name: React.PropTypes.string.isRequired
};